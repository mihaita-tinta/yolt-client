package com.mih.yolt.client.services;

import com.mih.yolt.client.config.HttpConfiguration;
import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static com.mih.yolt.client.services.UserSiteServiceTest.TOKEN;
import static com.mih.yolt.client.services.UserSiteServiceTest.USER_ID;

@SpringBootTest(classes = {HttpConfiguration.class, AccountService.class})
class AccountServiceTest {

    @Autowired
    AccountService service;

    @Test
    public void test() {
            service.getAccounts(AccessToken.of(TOKEN), User.of(USER_ID))
            .log()
            .blockLast();
    }
}