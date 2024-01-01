package com.habeebcycle.gcpstorage.router;

import com.habeebcycle.gcpstorage.exception.CustomResponseStatusException;
import org.slf4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Component
public class DefaultRouter {

    private static final Logger LOG = getLogger(DefaultRouter.class);

    @Bean
    @Order(999)
    public RouterFunction<ServerResponse> defaultRouterFunction() {
        return route(path("/**"), this::defaultResponse);
    }

    public Mono<ServerResponse> defaultResponse(final ServerRequest serverRequest) {
        final String path = serverRequest.path();
        LOG.info("Requested url or resource {} is not found. Sending not found error response.", path);
        return Mono.error(new CustomResponseStatusException(NOT_FOUND, "Resource requested not found."));
    }
}
