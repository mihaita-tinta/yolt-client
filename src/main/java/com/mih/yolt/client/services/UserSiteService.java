package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class UserSiteService {

    private final WebClient apiClient;

    public UserSiteService(WebClient apiClient) {
        this.apiClient = apiClient;
    }

    public Mono<RedirectResponse> getRedirectUrl(AccessToken accessToken, User user, Site site) {
        return apiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("v1/users/{USER_ID}/connect")
                        .queryParam("site", site.getId())
                        .queryParam("redirectUrlId", "21f81453-0857-408a-9e7f-755c65c340c0")
                        .build(user.getId()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .header("PSU-IP-Address", "ff39:6773:c03c:48e8:5b49:492a:d198:4b05")
                .retrieve()
                .bodyToMono(RedirectResponse.class);
    }


    public Mono<UserSiteActivateResponse> getUserSiteActivated(AccessToken accessToken, User user, UserSiteActivateRequest req) {
        return apiClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("v1/users/{USER_ID}/user-sites")
                        .build(user.getId()))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, accessToken.getTokenType() + " " + accessToken.getAccessToken())
                .header("PSU-IP-Address", "ff39:6773:c03c:48e8:5b49:492a:d198:4b05")
                .bodyValue(req)
                .retrieve()
                .bodyToMono(UserSiteActivateResponse.class);
    }


}
