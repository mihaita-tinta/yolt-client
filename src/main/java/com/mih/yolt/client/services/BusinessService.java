package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.*;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
public class BusinessService {
    private final AccessTokenService accessTokenService;
    private final RequestTokenService requestTokenService;
    private final SiteService siteService;
    private final UserSiteService userSiteService;
    private final AccountService accountService;
    private final UserService userService;
    private final ScaService scaService;

    public BusinessService(AccessTokenService accessTokenService, RequestTokenService requestTokenService, SiteService siteService, UserSiteService userSiteService, AccountService accountService, UserService userService, ScaService scaService) {
        this.accessTokenService = accessTokenService;
        this.requestTokenService = requestTokenService;
        this.siteService = siteService;
        this.userSiteService = userSiteService;
        this.accountService = accountService;
        this.userService = userService;
        this.scaService = scaService;
    }

    public Flux<Account> automaticallyGetAccounts() {
        return automaticallyGetAccounts(true);
    }

    public Flux<Account> automaticallyGetAccounts(boolean withSca) {
        String requestToken = requestTokenService.getToken();

        return accessTokenService.getAccessToken(requestToken)
                .flatMapMany(accessToken -> userService.getUser(accessToken)
                                       .flatMapMany(user -> siteService.getSites()
                                                        .map(list -> list.get(0))
                                               .flatMap(site -> userSiteService.getRedirectUrl(accessToken, user, site))
                                               .flatMapMany(res -> {
                                                   Mono<String> maybeSca = withSca ? scaService.getRedirect(res.getRedirect())
                                                           .flatMap(s -> scaService.postRedirect(res.getRedirect().getState()))
                                                           : Mono.just("https://yoltbank.sandbox.yolt.io/yoltbank/yolt-test-bank/authorize?redirect_uri=https://example.com&state=39db384d-56e3-4a17-b83a-1c366795b4b7");
                                                   return maybeSca
                                                           .flatMap(url -> userSiteService.getUserSiteActivated(accessToken, user, UserSiteActivateRequest.of(url))
                                                                                        .delayElement(Duration.ofSeconds(2)))
                                                                        .flatMapMany(userSite -> accountService.getAccounts(accessToken, user));
                                               })

                                       ));
    }

}
