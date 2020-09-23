package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.RedirectUrl;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ScaService {
    private static final Logger log = getLogger(ScaService.class);

    private final WebClient sandboxClient;

    public ScaService(@Qualifier("sandboxClient") WebClient sandboxClient) {
        this.sandboxClient = sandboxClient;
    }

    public Mono<String> getRedirect(RedirectUrl url) {

        return sandboxClient.get()
                .uri(url.getUrl())
                .retrieve()
                .bodyToMono(String.class);
    }


    public Mono<String> postRedirect(String state) {

        return sandboxClient.post()
                .uri("https://yoltbank.sandbox.yolt.io/yoltbank/yolt-test-bank/authorize?redirect_uri=https://example.com&state=" + state)
                .bodyValue("persona=LIGHT&tokenDuration=7776000&consentFlow=single_redirect&approve=Yes")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .exchange()
                .map(res -> {
                    log.info("postRedirectMono - state: {}", state);
                    return res.headers().header("Location").get(0);
                })
                .doOnNext(s -> log.info("postRedirectMono - redirectBackUrl: {}", state));
    }
}
