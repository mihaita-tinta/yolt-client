package com.mih.yolt.client.config;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import static com.mih.yolt.client.config.Securities.generateCertificateFromDER;
import static com.mih.yolt.client.config.Securities.parseDERFromPEM;

@Configuration
public class HttpConfiguration {

    @Value("${yolt.host:https://api.sandbox.yolt.io}")
    String yoltHost;

    @Bean
    @Primary
    public WebClient apiClient() throws InvalidKeySpecException, NoSuchAlgorithmException, CertificateException, IOException {
        byte[] pem = Files.readAllBytes(Paths.get(new ClassPathResource("tls-private-key.pem").getFile().getAbsolutePath()));
        byte[] keyBytes = parseDERFromPEM(pem, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");
        byte[] cer = Files.readAllBytes(Paths.get(new ClassPathResource("tls-certificate.pem").getFile().getAbsolutePath()));
        byte[] cerBytes = parseDERFromPEM(cer, "-----BEGIN CERTIFICATE-----", "-----END CERTIFICATE-----");

        RSAPrivateKey privateKey = Securities.generatePrivateKeyFromDER(keyBytes);
        X509Certificate certificate = generateCertificateFromDER(cerBytes);

        SslContext sslContext = SslContextBuilder
                .forClient()
                .keyManager(privateKey, certificate)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(sslContextSpec -> sslContextSpec.sslContext(sslContext));
        ClientHttpConnector connector = new ReactorClientHttpConnector(httpClient);

        return WebClient.builder()
                .baseUrl(yoltHost)
                .clientConnector(connector)
                .build();
    }


    @Bean
    public WebClient getWebClientSandbox() {
        ClientHttpConnector connector = new ReactorClientHttpConnector(HttpClient.create());

        return WebClient.builder()
                .baseUrl("https://yoltbank.sandbox.yolt.io")
                .clientConnector(connector)
                .build();
    }

}
