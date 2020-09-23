package com.mih.yolt.client.services;

import com.mih.yolt.client.config.HttpConfiguration;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {HttpConfiguration.class, RequestTokenService.class})
class RequestTokenServiceTest {
    private static final Logger log = LoggerFactory.getLogger(RequestTokenServiceTest.class);

    @Autowired
    RequestTokenService requestTokenService;

    @Test
    public void test() {
        log.info("test - request token: {}", requestTokenService.getToken());
    }

}
