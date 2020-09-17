package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class AccessTokenService {
    private final WebClient apiClient;

    public AccessTokenService(WebClient apiClient) {
        this.apiClient = apiClient;
    }

    public Mono<AccessToken> getYoltAccessToken(String requestToken) {
        return apiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/tokens")
                        .queryParam("request_token", requestToken)
                        .queryParam("grant_type", "client_credentials")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .retrieve()
                .bodyToMono(AccessToken.class);
    }
}
