package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class AccessTokenService {
    private final WebClient apiClient;

    public AccessTokenService(WebClient apiClient) {
        this.apiClient = apiClient;
    }

    public Mono<AccessToken> getAccessToken(String requestToken) {
        // FIXME the access token should be cached until it expires
        // and only then we should request a new one
        return apiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/tokens")
                        .queryParam("request_token", requestToken)
                        .queryParam("grant_type", "client_credentials")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .exchange()
                .flatMap(res -> {

                    if (res.statusCode().is2xxSuccessful())
                        return res.bodyToMono(AccessToken.class);

                    return res.bodyToMono(String.class)
                            .flatMap(s -> Mono.error(new IllegalArgumentException("error: " + s)));

                });
    }
}
