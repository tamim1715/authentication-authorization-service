package com.tamim.auth.security.jwt;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
@Slf4j
public class KeyProvider {

    @Value("${jwt.private-key}")
    private String privateKeyPath;

    @Value("${jwt.public-key}")
    private String publicKeyPath;

    private final ResourceLoader resourceLoader;

    public PrivateKey getPrivateKey() {
        try {
            String key = readKey(privateKeyPath);

            byte[] decoded = Base64.getDecoder().decode(key);

            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decoded);

            return KeyFactory.getInstance("RSA").generatePrivate(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load private key", e);
        }
    }

    public PublicKey getPublicKey() {
        try {
            String key = readKey(publicKeyPath);

            byte[] decoded = Base64.getDecoder().decode(key);

            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);

            return KeyFactory.getInstance("RSA").generatePublic(keySpec);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load public key", e);
        }
    }

    private String readKey(String location) throws Exception {
        Resource resource = resourceLoader.getResource(location);

        try (InputStream is = resource.getInputStream()) {
            String key = new String(is.readAllBytes());
            return key
                    .replaceAll("-----\\w+ PRIVATE KEY-----", "")
                    .replaceAll("-----\\w+ PUBLIC KEY-----", "")
                    .replaceAll("\\s", "");
        }
    }
}
