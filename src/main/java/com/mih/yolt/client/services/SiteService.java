package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.domain.Site;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class SiteService {
    private final WebClient apiClient;
    private final AccessTokenService accessTokenService;
    private final RequestTokenService requestTokenService;

    public SiteService(WebClient apiClient, AccessTokenService accessTokenService, RequestTokenService requestTokenService) {
        this.apiClient = apiClient;
        this.accessTokenService = accessTokenService;
        this.requestTokenService = requestTokenService;
    }


    public Mono<List<Site>> getSites() {
        ParameterizedTypeReference<List<Site>> typeRef = new ParameterizedTypeReference<List<Site>>() {
        };

        return accessTokenService.getAccessToken(requestTokenService.getToken())
                .flatMap(accessToken -> apiClient.get()
                                            .uri(uriBuilder -> uriBuilder
                                                    .path("/v2/sites")
                                                    .build())
                                            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                                            .header(HttpHeaders.AUTHORIZATION, accessToken.getAuthorizationHeaderValue())
                                            .retrieve()
                                            .bodyToMono(typeRef));
    }
}
