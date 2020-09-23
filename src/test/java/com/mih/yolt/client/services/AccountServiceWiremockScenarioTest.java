package com.mih.yolt.client.services;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import com.github.tomakehurst.wiremock.stubbing.Scenario;
import com.mih.yolt.client.config.HttpConfiguration;
import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.domain.Account;
import com.mih.yolt.client.domain.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.stream.IntStream;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.time.Duration.ofMillis;
import static reactor.retry.Retry.any;
import static reactor.retry.Retry.anyOf;

@SpringBootTest(classes = {HttpConfiguration.class, AccountService.class},
        properties = "yolt.host=http://localhost:8080")
class AccountServiceWiremockScenarioTest {
    private static final Logger log = LoggerFactory.getLogger(AccountServiceWiremockScenarioTest.class);
    private static final String ONE_ACCOUNT = "{\n" +
            "        \"accountHolder\": \"John Doe\",\n" +
            "        \"accountReferences\": {\n" +
            "          \"bban\": \"5390 0754 7034\",\n" +
            "          \"iban\": \"NL79ABNA9455762810\",\n" +
            "          \"maskedPan\": \"XXXXXXXXXX1234\",\n" +
            "          \"pan\": \"5500000000000004\",\n" +
            "          \"sortCodeAccountNumber\": \"12-34-5612345678\"\n" +
            "        },\n" +
            "        \"balance\": 100.45,\n" +
            "        \"bankSpecific\": {\n" +
            "          \"additionalProp1\": \"string\",\n" +
            "          \"additionalProp2\": \"string\",\n" +
            "          \"additionalProp3\": \"string\"\n" +
            "        },\n" +
            "        \"creditCardAccount\": {\n" +
            "          \"availableCredit\": 200.0,\n" +
            "          \"creditLimit\": 1000.0,\n" +
            "          \"linkedAccount\": \"14H8IY710471984729847\"\n" +
            "        },\n" +
            "        \"currency\": \"EUR\",\n" +
            "        \"externalId\": \"14H8IY710471984729847\",\n" +
            "        \"id\": \"be44b325-de6d-4123-81a9-2c67e6230253\",\n" +
            "        \"interestRate\": 0.04,\n" +
            "        \"name\": \"John's Credit Card Account-0\",\n" +
            "        \"product\": \"Gold account.\",\n" +
            "        \"status\": \"ENABLED\",\n" +
            "        \"type\": \"CREDIT_CARD\",\n" +
            "        \"usage\": \"PRIV\",\n" +
            "        \"userSite\": {\n" +
            "          \"siteId\": \"be44b325-de6d-4993-81a9-2c67e6230253\",\n" +
            "          \"userSiteId\": \"[(${userSiteId})]\"\n" +
            "        }\n" +
            "      }";

    WireMockServer mockServer = new WireMockClassRule(8080);

    @Autowired
    AccountService service;

    @Test
    public void testCallsWorkAlmostEveryTime() {

        mockServer
                .stubFor(WireMock.get(WireMock.urlPathMatching("/v1/users/.*/accounts*"))
                        .inScenario("get-accounts")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(ONE_ACCOUNT)
                                .withStatus(HttpStatus.OK.value())
                        )
                        .willSetStateTo("user-fail")
                );

        mockServer
                .stubFor(WireMock.get(WireMock.urlPathMatching("/v1/users/.*/accounts*"))
                        .inScenario("get-accounts")
                        .whenScenarioStateIs("user-fail")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        )
                        .willSetStateTo(Scenario.STARTED)
                );

        IntStream.range(0, 10)
                .forEach(i -> {
                    Flux<Account> maybeAccounts = service.getAccounts(AccessToken.of("abc-123"), User.of("user-id-123"));
                    if (i % 2 == 0) {
                        StepVerifier
                                .create(maybeAccounts)
                                .expectNextMatches(account -> "John Doe" .equals(account.getAccountHolder()))
                                .expectComplete()
                                .verify();
                    } else {
                        StepVerifier
                                .create(maybeAccounts)
                                .expectError(WebClientResponseException.class)
                                .verify();
                    }
                });

        verify(exactly(10), getRequestedFor(urlEqualTo("/v1/users/user-id-123/accounts")));
    }

    @Test
    public void testRetryBackoff() {

        mockServer
                .stubFor(WireMock.get(WireMock.urlPathMatching("/v1/users/.*/accounts*"))
                        .inScenario("get-accounts")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        )
                        .willSetStateTo("user-ok")
                );

        mockServer
                .stubFor(WireMock.get(WireMock.urlPathMatching("/v1/users/.*/accounts*"))
                        .inScenario("get-accounts")
                        .whenScenarioStateIs("user-ok")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withBody(ONE_ACCOUNT)
                                .withStatus(HttpStatus.OK.value())
                        )
                        .willSetStateTo(Scenario.STARTED)
                );

        Flux<Account> accounts = service.getAccounts(AccessToken.of("abc-123"), User.of("user-id-123"))
                                        .retryWhen(Retry.backoff(1, Duration.ofSeconds(1)));
        StepVerifier
                .create(accounts)
                .expectNextMatches(account -> "John Doe" .equals(account.getAccountHolder()))
                .expectComplete()
                .verify();


        verify(exactly(2), getRequestedFor(urlEqualTo("/v1/users/user-id-123/accounts")));
    }

    @Test
    public void testRetryPerException() {

        mockServer
                .stubFor(WireMock.get(WireMock.urlPathMatching("/v1/users/.*/accounts*"))
                        .inScenario("get-accounts")
                        .whenScenarioStateIs(Scenario.STARTED)
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.SERVICE_UNAVAILABLE.value())
                        )
                        .willSetStateTo("user-ok")
                );

        mockServer
                .stubFor(WireMock.get(WireMock.urlPathMatching("/v1/users/.*/accounts*"))
                        .inScenario("get-accounts")
                        .whenScenarioStateIs("user-ok")
                        .willReturn(aResponse()
                                .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)
                                .withStatus(HttpStatus.BAD_REQUEST.value())
                        )
                        .willSetStateTo(Scenario.STARTED)
                );

        Flux<Account> accounts = service.getAccounts(AccessToken.of("abc-123"), User.of("user-id-123"))
                                    .retryWhen(anyOf(WebClientResponseException.ServiceUnavailable.class)
                                                .exponentialBackoff(ofMillis(10), ofMillis(1000)).retryMax(2));
        StepVerifier
                .create(accounts)
                .expectError(WebClientResponseException.BadRequest.class)
                .verify();


        verify(exactly(2), getRequestedFor(urlEqualTo("/v1/users/user-id-123/accounts")));
    }

    @BeforeEach
    public void before() throws InterruptedException {
        mockServer.start();
        while (!mockServer.isRunning()) {
            Thread.sleep(100L);
            log.info("before - waiting for wiremock server to start . . .");
        }
    }

    @AfterEach
    public void after() {
        mockServer.stop();
    }
}
