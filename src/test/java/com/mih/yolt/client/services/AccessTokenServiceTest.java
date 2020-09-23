package com.mih.yolt.client.services;

import com.mih.yolt.client.config.HttpConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {HttpConfiguration.class, AccessTokenService.class, RequestTokenService.class})
class AccessTokenServiceTest {

    @Autowired
    AccessTokenService service;
    @Autowired
    RequestTokenService requestTokenService;

    @Test
    public void test () {
            service.getAccessToken(requestTokenService.getToken())
                    .log()
                    .block();
    }

}
