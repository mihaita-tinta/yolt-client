package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.config.HttpConfiguration;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@SpringBootTest(classes = {HttpConfiguration.class, SiteService.class})
class SiteServiceTest {

    @Autowired
    SiteService service;

    @MockBean
    AccessTokenService accessTokenService;
    @MockBean
    RequestTokenService requestTokenService;

    @Test
    public void test() {
        String token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIn0..a-3lT69lsWEDRxh7cBIYhw.FDPhvvOwTZbAkvN3fQx3_L-3ztbwwVzShvcqYRSPINe0MSXMBQszYrHK3p3GbKSimbrNkAg8skJ-IZJpfKZC54tS2RgayFE-zWOZmCxVCF2hHM_M66IOy4w9Xed7-0yaPdlbMquC_cA700I-_xkH-E0aI1Zb3gUqoe_cHUOwoKa3zN_6JPT-lIBNcqpQEO6BeNQaRBts2pEjdIcs_3M9Zw.WX3OAsoIfMjFGJQRkZ76EDTiXDgCXmlWR2jpNOKA8u4";

        when(accessTokenService.getAccessToken(ArgumentMatchers.any()))
                .thenReturn(Mono.just(AccessToken.of(token)));

        service.getSites()
                .log()
                .block();
    }

}
