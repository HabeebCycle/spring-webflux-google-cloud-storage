package com.habeebcycle.gcpstorage.router;

import com.google.cloud.storage.Blob;
import com.habeebcycle.gcpstorage.service.StorageService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class ApplicationRouter {

    private final StorageService storageService;

    public ApplicationRouter(final StorageService storageService) {
        this.storageService = storageService;
    }

    @Bean
    @Order(1)
    public RouterFunction<ServerResponse> apiRouterFunction() {
        return route()
                .POST("/upload/files/{location}", this::handleFileUpload)
                .GET("/retrieve/files/{location}/{filename}", this::handleFetchFile)
                .DELETE("/remove/files/{location}/{filename}", this::handleDeleteFile)
                .DELETE("/remove/folders/{folderName}", this::handleDeleteFolder)
                .build();
    }

    @NonNull
    public Mono<ServerResponse> handleFileUpload(final ServerRequest serverRequest) {
        var location = serverRequest.pathVariable("location");

        Mono<List<String>> fileResponseMono = serverRequest.multipartData()
            .flatMap(parts -> {
                List<Part> fileParts = parts.get("files");

                //USAGE: If there is any need of field values, uncomment the code below:
                //FormFieldPart formField = (FormFieldPart) (parts.get("fields").getFirst());
                //formField.value()

                List<FilePart> filePartList = fileParts.stream()
                    .map(p -> (FilePart) p)
                    .toList();

                return Flux.fromIterable(filePartList)
                    .flatMap(filePart -> DataBufferUtils.join(filePart.content())
                        .map(DataBuffer::asInputStream)
                        .map(inputStream -> storageService
                            .uploadFile(inputStream, filePart.filename(), location))
                        .filter(StringUtils::isNotBlank))
                    .collectList();
            });

        return ServerResponse.ok().body(fileResponseMono, new ParameterizedTypeReference<>() {});
    }

    @NonNull
    public Mono<ServerResponse> handleFetchFile(final ServerRequest serverRequest) {
        var location = serverRequest.pathVariable("location");
        var filename = serverRequest.pathVariable("filename");

        Blob blob = storageService.getFile(filename, location);
        if (blob != null) {
            return ServerResponse.ok()
                .contentType(MediaType.parseMediaType(blob.getContentType()))
                .bodyValue(blob.getContent());
        }

        return ServerResponse.notFound().build();
    }

    @NonNull
    public Mono<ServerResponse> handleDeleteFile(final ServerRequest serverRequest) {
        var location = serverRequest.pathVariable("location");
        var filename = serverRequest.pathVariable("filename");

        return ServerResponse.ok()
                .bodyValue(storageService.deleteFile(filename, location));
    }

    @NonNull
    public Mono<ServerResponse> handleDeleteFolder(final ServerRequest serverRequest) {
        var location = serverRequest.pathVariable("folderName");

        return ServerResponse.ok()
                .bodyValue(storageService.deleteFolder(location));
    }
}
