package com.habeebcycle.gcpstorage.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@Component
@Order(-980)
public class RequestResponseFilter implements WebFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestResponseFilter.class);

    @Override
    @NonNull
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        final ServerHttpRequest request = exchange.getRequest();
        final ServerHttpResponse response = exchange.getResponse();

        final long startMillis = System.currentTimeMillis();

        var requestHeaders = new HashMap<>();
        var requestHeaderMap = request.getHeaders().toSingleValueMap();
        requestHeaderMap.keySet().forEach(header -> {
            if(!header.equalsIgnoreCase("Authorization"))
                requestHeaders.put(header, requestHeaderMap.get(header));
        });

        LOGGER.info("REQUEST_LOGGER: Received request [{}] with URI: [{}] with request headers [{}] with request parameters [{}] ",
                request.getMethod(), request.getURI().getPath(), requestHeaders, request.getQueryParams());

        response.beforeCommit(() -> {
            LOGGER.info("RESPONSE_LOGGER: Sending response with status [{}] response headers [{}]",
                    response.getStatusCode(), response.getHeaders().toSingleValueMap());
            return Mono.empty();
        });

        return chain.filter(exchange).doFinally(aVoid -> LOGGER.info("DURATION_LOGGER: Total time elapsed to process the request: [{} ms] for request [{}]",
                System.currentTimeMillis() - startMillis, exchange.getRequest().getURI().getPath()));
    }
}
