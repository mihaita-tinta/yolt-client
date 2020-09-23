package com.mih.yolt.client.rest;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.cloud.contract.wiremock.restdocs.SpringCloudContractRestDocs;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.springframework.restdocs.webtestclient.WebTestClientRestDocumentation.document;

@RunWith(SpringRunner.class)
@SpringBootTest(properties = "yolt.host=http://localhost:${wiremock.server.port}")
@AutoConfigureRestDocs
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 0)
public class SiteResourceTest {

    @Autowired
    private WebTestClient client;

    @Test
    public void listSites() {


        client.get()
                .uri("/sites")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .consumeWith(s -> System.out.println(new String(s.getResponseBody())))
                .jsonPath("$[0].id").isEqualTo("site-id-12345")
                .jsonPath("$.length()").isEqualTo(1)
                .consumeWith(document("sites-get", SpringCloudContractRestDocs.dslContract()));
    }

}
