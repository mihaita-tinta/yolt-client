package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.domain.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private final WebClient apiClient;

    public UserService(WebClient apiClient) {
        this.apiClient = apiClient;
    }

    public Mono<User> getUser(AccessToken accessToken) {
        return apiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/users")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .retrieve()
                .bodyToMono(User.class);
    }
}
