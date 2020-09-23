# yolt-client
A sample spring boot application calling an API and showcasing how to test the java code without that API being available using [spring cloud contract wiremock](https://cloud.spring.io/spring-cloud-contract/reference/html/project-features.html#features-wiremock)

## Connecting to the Yolt sandbox
You can connect to the Yolt sandbox by following the next steps:
* [Getting started](https://developer.yolt.com/docs/getting-started)
* You should obtain a few files:
  * private-key.pem
  * public-key.pem
  * tls-certificate.pem
  * tls-csr.pem
  * tls-private-key.pem
* Move the files above to the `src/main/resources/sandbox` folder
* Login to the portal and search for `$CLIENT_ID` and `$REDIRECT_URL_ID`.
Update the `application.properties` file with your values:
```
yolt.client-id=758fe252-caa1-4afe-af54-6aee05900000
yolt.key-id=e2292eb3-b54b-4fe3-8b2b-6c94e8f40000
```
* Now it should be easy to get your first `requestToken`.
This helps you generating an access token that will be used in all your calls to Yolt.
Get an request token using `jose4j`.
You can run the `RequestTokenServiceTest` test and see the output
```text
0:40:04.612 [main] INFO com.mih.yolt.client.services.RequestTokenServiceTest - test - request token: eyJraWQiOm51bGwsImFsZyI6IlJTMjU2In0.eyJpc3MiOm51bGwsImp0aSI6IjMxNzA1Y2JjLTYzNGMtNDIzMi1iYTYyLWIwMTEwZWMxNzViMyIsImlhdCI6MTYwMDc2MDQwM30.pTn2WmMk2-YUI2kEtpzZdKEW12zfy9ueAXCzianFcgjDYWphFj6psvCNVoWUyoITpyVCkQSx7gefN-byYNkn5Pg2KQWZC1Lblu2glFbCH5FtNBMRHzcMwqNDbfWzcT0x1uWFa40NE8DWmi5XbU7Tk6hcWtS78ut_GT7F3smrQtY0Gh55d6xFkSvgnCBXt8kJBj3RP_NQVQ32wDSzK1WHCtvE6jxWQR9_iROnh7IFDdRNJQrEE2OZvfhuswDAaoa38WrKn8pko3dgXS03ArkqZJ5XyQpSzJ8v8h1zkOYV7aOzLc4MjqR-B_JQGy7HpazSpMJPOwx0W7aI_-rPPmi8bWWYZMnR-26jymSvYbQmzLXRT0jvp_lcqfiXVHVaV9ASOYatE2RTvx51tI17-ujzOKhPj-Jy_dBmN1XzPzoZbty80EQjDMSAEu06tDOqw_nUHwY7fVuMxvjvxdl6xNOZXk0XH_vhM8M4V7_0zPwf6Hs-9epnb9goxVmtF3e6Jh6WSjHamTqA61Ir-epTfD10qUU273-t-nazCVakgndpsSjeZc71ZSW_Vi4UnTMfRWAv8E7LWx0jOSBh3o0iq9oQncReMJgfXZDtX7KrXlkM6JpQIYR_Ljp8hHF86aaaa
```
* To get an access token, just pass the value above to `AccessTokenService.getAccessToken(requestToken)` method.
You can run the `AccessTokenServiceTest` test

```java
@Test
public void test () {
   service.getAccessToken(requestTokenService.getToken())
          .log()
          .block();
}
```
You should see in the log the access token:
```text
2020-09-22 10:43:42.980  INFO 6237 --- [ctor-http-nio-1] reactor.Mono.FlatMap.1                   : | onNext(YoltAccessToken{accessToken='eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIn0..a-3lT69lsWEDRxh7cBIYhw.FDPhvvOwTZbAkvN3fQx3_L-3ztbwwVzShvcqYRSPINe0MSXMBQszYrHK3p3GbKSimbrNkAg8skJ-IZJpfKZC54tS2RgayFE-zWOZmCxVCF2hHM_M66IOy4w9Xed7-0yaPdlbMquC_cA700I-_xkH-E0aI1Zb3gUqoe_cHUOwoKa3zN_6JPT-lIBNcqpQEO6BeNQaRBts2pEjdIcs_3M9Zw.WX3OAsoIfMjFGJQRkZ76EDTiXDgCXmlWR2jpNOKaaaa', tokenType='Bearer', expiresIn=600})
```
* Alternatively you can run the scripts provided in the Yolt portal:
```bash
CLIENT_ID=<enter-your-client-id>
REQUEST_TOKEN_PUBLIC_KEY_ID=<enter-your-key-id>
REQUEST_TOKEN=$(step crypto jwt sign --iss $CLIENT_ID \
  --kid $REQUEST_TOKEN_PUBLIC_KEY_ID \
  --key private-key.pem \
  --alg RS512 \
  --jti=`uuidgen` \
  --subtle)
curl https://api.sandbox.yolt.io/v1/tokens   --cert tls-certificate.pem   --key tls-private-key.pem   -H "Content-Type: application/x-www-form-urlencoded"   -d "grant_type=client_credentials&request_token=$REQUEST_TOKEN"
```

* Before getting the accounts an user should choose a site. Therefore we need to expose the list of available sites:
```java
    @Autowired
    SiteService service;

    @Test
    public void test() {
        String token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIn0..a-3lT69lsWEDRxh7cBIYhw.FDPhvvOwTZbAkvN3fQx3_L-3ztbwwVzShvcqYRSPINe0MSXMBQszYrHK3p3GbKSimbrNkAg8skJ-IZJpfKZC54tS2RgayFE-zWOZmCxVCF2hHM_M66IOy4w9Xed7-0yaPdlbMquC_cA700I-_xkH-E0aI1Zb3gUqoe_cHUOwoKa3zN_6JPT-lIBNcqpQEO6BeNQaRBts2pEjdIcs_3M9Zw.WX3OAsoIfMjFGJQRkZ76EDTiXDgCXmlWR2jpNOKA8u4";
        service.getSite(AccessToken.of(token))
                .log()
                .block();
    }
```
For the Yolt sandbox we can use the `Yolt test bank` to simulate getting accounts after an SCA process
```text
2020-09-22 10:46:18.537  INFO 6280 --- [ctor-http-nio-1] reactor.Mono.FlatMap.1                   : | onNext([YoltSite{id='33aca8b9-281a-4259-8492-1b37706af6db'name='Yolt test bank'}])
```
* You can check the service below on how to automatically authenticate the user against the `Yolt test bank`
 and be redirect to our app and then list all the available accounts.
 **Please note the `scaService` in reality will be something triggered by an user from their browser!**
```java

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
        String requestToken = requestTokenService.getToken();

        return accessTokenService.getAccessToken(requestToken)
                .flatMapMany(accessToken -> userService.getUser(accessToken)
                                       .flatMapMany(user -> siteService.getSite(accessToken)
                                                        .map(list -> list.get(0))
                                               .flatMap(site -> userSiteService.getRedirectUrl(accessToken, user, site))
                                               .flatMapMany(res -> scaService.getRedirect(res.getRedirect())
                                                                        .flatMap(s -> scaService.postRedirect(res.getRedirect().getState()))
                                                                    .flatMap(url -> userSiteService.getUserSiteActivated(accessToken, user, UserSiteActivateRequest.of(url))
                                                                                    .delayElement(Duration.ofSeconds(2)))
                                                                    .flatMapMany(userSite -> accountService.getAccounts(accessToken, user)))

                                       ));
    }

}
```
Example of some accounts below. Yolt can call your app with their webhook implementation or you can start polling Yolt to check the UserSite status - if the data is available.
```text
020-09-22 10:53:12.304  INFO 6408 --- [ctor-http-nio-1] reactor.Flux.MonoFlatMapMany.1           : onNext(YoltAccount{id='2a293238-c4cc-4088-8a03-db0fb6ed7355', accountHolder='null', userSite=UserSite{connectionStatus='null', id='null', lastDataFetchTime=null, lastDataFetchFailureReason='null'}, balance=6311.05, currency='EUR', type='CREDIT_CARD'})
2020-09-22 10:53:12.305  INFO 6408 --- [ctor-http-nio-1] reactor.Flux.MonoFlatMapMany.1           : onNext(YoltAccount{id='80d16a28-5f12-44e3-87b2-75451cd2787e', accountHolder='null', userSite=UserSite{connectionStatus='null', id='null', lastDataFetchTime=null, lastDataFetchFailureReason='null'}, balance=436091.1, currency='EUR', type='CURRENT_ACCOUNT'})
2020-09-22 10:53:12.305  INFO 6408 --- [ctor-http-nio-1] reactor.Flux.MonoFlatMapMany.1           : onNext(YoltAccount{id='fe81a69c-6d04-4284-8975-805f58180f41', accountHolder='null', userSite=UserSite{connectionStatus='null', id='null', lastDataFetchTime=null, lastDataFetchFailureReason='null'}, balance=5861.94, currency='EUR', type='SAVINGS_ACCOUNT'})
```

## Mocking dependencies
By default all the wiremock mappings can be found in the `src/test/resources/mappings` folder.
Check the [official wiremock documentation](http://wiremock.org/docs/stubbing/) on how to write such files.

### Static stubs
Running a test with a mocked API is very easy:
```java

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

        verify(lessThan(2), postRequestedFor(urlEqualTo("/v1/tokens")));
    }

}
```

### Scenarios
*WireMock supports state via the notion of [scenarios](http://wiremock.org/docs/stateful-behaviour/). A scenario is essentially a state machine whose states can be arbitrarily assigned. It starting state is always Scenario.STARTED. Stub mappings can be configured to match on scenario state, such that stub A can be returned initially, then stub B once the next scenario state has been triggered.*

#### Example even/odd calls
Let's say we want to test what happens if one out of two calls is failing.
The wiremock state machine is something like this:

![Wiremock state machine](./wiremock-state-machine.svg)

In our test we can instruct wiremock on how to perform the transition from one state to another

```java
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
```

#### Example retry

Usually when a service is unavailable, some sort of retry mechanism is required.
We can add a simple retry logic on our `Flux` with `.retryWhen(Retry.backoff(1, Duration.ofSeconds(1)))` 
```java
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
```

In some cases you need to retry for only specific exceptions

`.retryWhen(anyOf(WebClientResponseException.ServiceUnavailable.class)
                                                .exponentialBackoff(ofMillis(10), ofMillis(1000)).retryMax(2))`

### Automatically record stubs

_WireMock can create stub mappings from requests it has received. Combined with its proxying feature this allows you to [record](http://wiremock.org/docs/record-playback/) stub mappings from interaction with existing APIs._

![wiremock record](./wiremock-record.svg)

Our `local-sandbox` folder contains a `docker` environment.
This contains:
* `my-wiremock` a [customized](https://github.com/mihaita-tinta/wiremock-body-transformer) wiremock instance
* `routing` just a proxy calling the `Yolt` sandbox instance with some tls certificates required for mTLS (These files you should already have from the Yolt onboarding steps)
  You need to place them in the here: `local-sandbox/routing/tls-certificate.pem` and `local-sandbox/routing/tls-private-key.pem` and then build your own `routing` image for your own `yolt` client

```shell script
docker-compose up
```

![local ](./local-sandbox.png)

The next step is to configure `wiremock` to start recording the traffic it gets.
Go [here](http://localhost:8080/__admin/recorder/)
![local ](./wiremock-recorder.png)

Generate some traffic. Make sure to point to the running wiremock instance: `yolt.host=http://localhost:8080`
```java
@SpringBootTest
class BusinessServiceTest {
    private static final Logger log = LoggerFactory.getLogger(BusinessServiceTest.class);

    @Autowired
    BusinessService service;

    @Test
    public void test() {
        service.automaticallyGetAccounts()
                .log()
                .blockLast();
    }
}
```
Only after stopping the recording `wiremock` generated the stub files.
![recorded ](./wiremock-recorded.png)

They can be found in the wiremock `mappings` folder

![recorded ](./auto-generated-stubs.png)

With our [custom wiremock extension](https://github.com/mihaita-tinta/wiremock-body-transformer) we can add different functionalities using `thymeleaf` expressions.
This can create lists with different lengths, create/read JWTs, store variables, work with dates, UUIDs, counters etc

```json
{"session": [
          [# th:each="element,index : ${session}" ]
            "[(${element.key})]" : "[(${element.value})]"[# th:if="!${index.last}" ],[/]
          [/]
          [# th:each="element,index : ${utils.list(5)}" ]
            "key-[(${index.current})]": [(${index.current})]
          [# th:if="!${index.last}" ],[/]
          [/]
          ,"jwt":"[(${utils.jwt('123')})]",
          "var":"[(${utils.accessToken(xjwt).getClaimValue('sub')})]",
          "date":"[(${#temporals.formatISO(#temporals.createNow())})]",
          "counter1":"[(${counter.incrementAndGet()})]", "counter2":"[(${counter.incrementAndGet()})]",
          "random":"[(${utils.random().nextInt(1000)})]",
          "uuid":"[(${utils.uuid()})]"
    ]
}
```
Output:
```json
{"session": [
          "key-0": 0,
          "key-1": 1,
          "key-2": 2,
          "key-3": 3,
          "key-4": 4,
          "jwt":"eyJhbGciOiJSUzI1NiJ9.eyJleHAiOjE2MDA4NTcxMTEsImp0aSI6Im1PbDZ3MUJYTloxc0hqb041Mk9BQ1EiLCJpYXQiOjE2MDA4NTY1MTEsIm5iZiI6MTYwMDg1NjM5MSwic3ViIjoiMTIzIn0.TIKEsDl6QxVay4-EpztAKYZmMAjCFirU-mrZniq9ZUtYnfK8h97xpRE7IcxCT17C4RVJBJ45CEBFK3X5loLKJu-5XUjUQX-_ghHAsHkAEQmSMgEQZY5NBce8G6CCR_qKcUxk1ryuXhmVf45G7uK8MOazb_rDae-Zo1z10IJbSxOjOQ6V56TkDAH_3fHcaZKoHGswF8bxVvbUseZCI7so2VX_vt1bipI_hBgiDG9ThiUZZFhyqpNs2-DYnUrexx2rEEAFX8Cz0-rZRsbimqFSaKA4mg24w8rvFzjxfaoSArny1p0iRy8_o0tLwKHoFeqsqizMRyhWFgKEObBWQZEQIw",
          "var":"123",
          "date":"2020-09-23T10:21:51.153+0000",
          "counter1":"1", "counter2":"2",
          "random":"211",
          "uuid":"9d57db85-0acf-40e0-9ac0-2f857a520713"
    ]
}
```
### Testing our own API

One way to test our API is to connect it to a local wiremock instance statically configured by us.
Based on some existing conditions _what `Yolt` returns_, we can create various contracts for our API.

The end result for our test below is that we are creating an *up to date* documentation based on our `asciidocs` file.
This means that everytime we change our tests we are also updating the content of our requests and responses.
In practice we should have an 1-1 documentation with what the application does.

![asciidocs](./asciidocs.png)


We are also creating the [stubs](https://cloud.spring.io/spring-cloud-contract/reference/html/project-features.html#features-stub-runner)
that can be used by our own clients (someone calling our Yolt-Client API).
This means that it will be very easy for someone to integrate with us, because all their implementation should be done based on the contracts we define.
_If there will be any stubs available from Yolt at some point, we could do the same._

```java
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
```

Sample groovy contract:

```groovy
import org.springframework.cloud.contract.spec.Contract

Contract.make {
    request {
        method 'GET'
        urlPath('/sites') 
    }
    response {
        status 200
        body('''[{"id":"site-id-12345","name":"Yolt Test Bank"}]''')
        headers {
            header('''Content-Type''', '''application/json''')
        }
    }
}
```

