package com.mih.yolt.client.rest;

import com.mih.yolt.client.domain.Site;
import com.mih.yolt.client.services.SiteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class SiteResource {

    private final SiteService siteService;


    public SiteResource(SiteService siteService) {
        this.siteService = siteService;
    }

    @GetMapping("/sites")
    public Flux<Site> getSites() {
        return siteService.getSites()
                .flatMapMany(Flux::fromIterable);
    }
}
