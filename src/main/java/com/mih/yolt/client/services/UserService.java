package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.domain.User;
import org.slf4j.Logger;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class UserService {
    private static final Logger log = getLogger(UserService.class);
    private final WebClient apiClient;

    public UserService(WebClient apiClient) {
        this.apiClient = apiClient;
    }

    public Mono<User> createUser(AccessToken accessToken) {
        return apiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/v2/users")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .exchange()
                .flatMap(res -> {
                    if (!res.statusCode().is2xxSuccessful()) {
                        return res.bodyToMono(String.class)
                                .flatMap(s -> Mono.error(new IllegalArgumentException("error: " + s)));

                    } else {
                        return res.bodyToMono(User.class)
                                .log();
                    }
                });
    }
}
