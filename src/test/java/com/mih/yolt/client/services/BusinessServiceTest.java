package com.mih.yolt.client.services;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BusinessServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BusinessServiceTest.class);

    @Autowired
    BusinessService service;

    @Test
    public void testCreateUserGetSitesAndGetRedirectUrl() {
        service.createUserGetSitesAndGetRedirectUrl()
                .log()
                .block();
    }

    @Test
    public void test() {
        service.automaticallyGetAccounts()
                .log()
                .blockLast();
    }

    @Test
    public void testWithoutSca() {
        service.automaticallyGetAccounts(false)
                .log()
                .blockLast();
    }


}
