package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.config.HttpConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {HttpConfiguration.class, SiteService.class, AccessTokenService.class, RequestTokenService.class})
class SiteServiceTest {

    @Autowired
    SiteService service;

    @Autowired
    AccessTokenService accessTokenService;
    @Autowired
    RequestTokenService requestTokenService;

    @Test
    public void test() {
       service.getSites()
                .log()
                .block();
    }

}
