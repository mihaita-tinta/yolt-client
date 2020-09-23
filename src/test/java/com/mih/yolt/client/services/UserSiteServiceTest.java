package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.*;
import com.mih.yolt.client.config.HttpConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {HttpConfiguration.class, UserSiteService.class})
class UserSiteServiceTest {
    static final String TOKEN = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIn0..S23tGWAXT_YqKeqPH9i-WA.9LVrGB-cwms22ZVjXJ40CfnwoWNou4LBc6yBdpIpbO_TTiN_Z1fPbqLqR6klr3aMxoZi8yxQaCtguKJy10dQ0snaJZWsmuYjPe-HRymNReQelk98JluyFMu4XhwGjiR1PuPGWxoZarhBroaXc4HOrgkask3vPybXwCHxpqWdySYRyvq47JrOnkasKwzoLMB4vz4kufADbZvwvgQzcw2bCA.lVTyLawS2SwVXAIy4FEjqXqsv4fpA7A4U_YLuod27TM";
    static final String USER_ID = "4e20238d-cfba-410a-b408-bc42540e35fe";
    static final String SITE_ID = "33aca8b9-281a-4259-8492-1b37706af6db";

    @Autowired
    UserSiteService service;

    @Test
    public void testGetRedirectUrl() {

        service.getRedirectUrl(AccessToken.of(TOKEN), User.of(USER_ID), Site.of(SITE_ID))
                .map(RedirectResponse::getRedirect)
                .map(RedirectUrl::getUrl)
                .log()
                .block();
    }

    @Test
    public void testActivate() {
        String url = "https://example.com/?code=eyJhbGciOiJub25lIn0.eyJleHAiOjE2MDgwNDIwNTcsInN1YiI6IkxJR0hUIiwiaWF0IjoxNjAwMjY2MDU3fQ.&state=71ae442a-3ce5-4147-8f31-f8cc8fc478a7";

        service.getUserSiteActivated(AccessToken.of(TOKEN), User.of(USER_ID), UserSiteActivateRequest.of(url))
                .map(UserSiteActivateResponse::getUserSite)
                .map(UserSite::getId)
                .log()
                .block();

    }
}