package com.mih.yolt.client.services;

import com.mih.yolt.client.config.HttpConfiguration;
import com.mih.yolt.client.domain.AccessToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(classes = {HttpConfiguration.class, AccessTokenService.class, RequestTokenService.class},
        properties = "yolt.host=http://localhost:${wiremock.server.port}")
@AutoConfigureWireMock(port = 0)
class AccessTokenServiceWiremockTest {


    @Autowired
    AccessTokenService service;
    @Autowired
    RequestTokenService requestTokenService;

    @Test
    public void test() {

        Mono<AccessToken> accessTokenMono = service.getAccessToken(requestTokenService.getToken());
        StepVerifier
                .create(accessTokenMono)
                .expectNextMatches(at -> at.getAccessToken().equals("dummy_access_token"))
                .expectComplete()
                .verify();

        verify(lessThan(5), postRequestedFor(urlEqualTo("/v1/tokens")));
    }

}
