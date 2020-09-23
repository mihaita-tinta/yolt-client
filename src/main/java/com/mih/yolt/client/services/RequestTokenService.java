package com.mih.yolt.client.services;

import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.NumericDate;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAPrivateKey;
import java.util.UUID;

import static com.mih.yolt.client.config.Securities.generatePrivateKeyFromDER;
import static com.mih.yolt.client.config.Securities.parseDERFromPEM;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class RequestTokenService {
    private static final Logger log = getLogger(RequestTokenService.class);

    @Value("${yolt.client-id}")
    String clientId;
    @Value("${yolt.key-id}")
    String keyId;

    public String getToken() {
        try {
            byte[] pem = Files.readAllBytes(Paths.get(new ClassPathResource("sandbox/private-key.pem").getFile().getAbsolutePath()));
            byte[] keyBytes = parseDERFromPEM(pem, "-----BEGIN PRIVATE KEY-----", "-----END PRIVATE KEY-----");

            RSAPrivateKey key = generatePrivateKeyFromDER(keyBytes);

            JwtClaims claims = new JwtClaims();

            String nonce = UUID.randomUUID().toString();
            NumericDate iatNumericDate = NumericDate.now();

            iatNumericDate.addSeconds(-1);
            claims.setIssuer(clientId);
            claims.setJwtId(nonce);
            claims.setIssuedAt(iatNumericDate);

            JsonWebSignature jws = new JsonWebSignature();
            jws.setPayload(claims.toJson());
            jws.setKey(key);
            jws.setKeyIdHeaderValue(keyId);
            jws.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);
            return jws.getCompactSerialization();
        } catch (Exception e) {
            log.error("getToken - Error while getting a token: {}", e.getMessage(), e);
            throw new IllegalStateException("could not generate token");
        }
    }
}
