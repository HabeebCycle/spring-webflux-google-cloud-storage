package com.habeebcycle.gcpstorage.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.DefaultPartHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class CodecsConfig implements WebFluxConfigurer {

    private final int maxUploadCount;
    private final long maxRequestSize;
    private final int maxInMemorySize;

    public CodecsConfig(@Value("${app.config.max-upload-count}") int maxUploadCount,
                        @Value("${app.config.max-request-size}") long maxRequestSize,
                        @Value("${app.config.max-in-mem-size}") int maxInMemorySize) {
        this.maxUploadCount = maxUploadCount;
        this.maxRequestSize = maxRequestSize;
        this.maxInMemorySize = maxInMemorySize;
    }


    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer serverConfig) {
        DefaultPartHttpMessageReader defaultPartHttpMessageReader = new DefaultPartHttpMessageReader();
        defaultPartHttpMessageReader.setMaxParts(maxUploadCount);
        defaultPartHttpMessageReader.setMaxDiskUsagePerPart(maxRequestSize);
        defaultPartHttpMessageReader.setEnableLoggingRequestDetails(true);

        MultipartHttpMessageReader httpMessageReader = new MultipartHttpMessageReader(defaultPartHttpMessageReader);
        httpMessageReader.setEnableLoggingRequestDetails(true);

        serverConfig.defaultCodecs().multipartReader(httpMessageReader);
        serverConfig.defaultCodecs().maxInMemorySize(maxInMemorySize);
    }

}
