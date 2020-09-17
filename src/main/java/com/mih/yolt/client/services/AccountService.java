package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.domain.Account;
import com.mih.yolt.client.domain.User;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
public class AccountService {

    private final WebClient apiClient;

    public AccountService(WebClient apiClient) {
        this.apiClient = apiClient;
    }

    public Flux<Account> getAccounts(AccessToken accessToken, User user) {
        return apiClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("v1/users/{userId}/accounts")
                        .build(user.getId()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .retrieve()
                .bodyToFlux(Account.class);
    }
}
