package com.mih.yolt.client.services;

import com.mih.yolt.client.domain.AccessToken;
import com.mih.yolt.client.config.HttpConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {HttpConfiguration.class, UserService.class})
class UserServiceTest {

    @Autowired
    UserService service;

    @Test
    public void test() {
        String token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2Q0JDLUhTNTEyIn0..xikxRzw_ZgMT53nE-s4fbg.EZakWmCuMkHWVuGXequpH-Bgv-GLA9yhJ87JRy38YeXoYlKZnkgs_uED5b2-vYLuINP60rakSX8Lu6KOFVPURx4yYYjaen0tQM8RzpuzfBvSIqMNF7MChoOR8tSEeD93B9xdFtHkn-x5kNRDVDKhTxejUz5fhHCt0L2kvNcN0SmPNQeyu9qDecyZli1KWRr-wprMr9GpohkEXNiZbp2A_g.97vouUt40PqZo8jbvU0sQmZOJ8EDB0In9oCTmfWmmP0";
        service.createUser(AccessToken.of(token))
                .log()
                .block();


    }
}
