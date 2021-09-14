package com.roomsbooking.backend.config;

import com.roomsbooking.backend.exception.AuthException;
import java.io.IOException;
import java.io.InputStream;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

/**
 * Class responsible for configuring beans required for JTW signing and validation.
 */
@Slf4j
@Configuration
public class JwtConfiguration {

    @Value("${security.jwt.keystore-localisation}")
    private String keyStorePath;

    @Value("${security.jwt.keystore-password}")
    private String keyStorePassword;

    @Value("${security.jwt.key-alias}")
    private String keyAlias;

    @Value("${security.jwt.private-key-passphrase}")
    private String privateKeyPassphrase;

    /**
     * Method responsible for registering bean responsible for loading the keystore.
     *
     * @return bean of type {@link KeyStore}
     */
    @Bean
    public KeyStore keyStore() {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream resourceAsStream = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(keyStorePath);
            keyStore.load(resourceAsStream, keyStorePassword.toCharArray());
            return keyStore;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            log.error("Unable to load keystore: " + keyStorePath);
        }
        throw AuthException.jwtKeystoreError();
    }

    /**
     * Method responsible for registering bean responsible for loading private key from the
     * keystore.
     *
     * @return bean of type {@link RSAPrivateKey}
     */
    @Bean
    public RSAPrivateKey jwtSigningKey(KeyStore keyStore) {
        try {
            Key key = keyStore.getKey(keyAlias, privateKeyPassphrase.toCharArray());
            if (key instanceof RSAPrivateKey) {
                return (RSAPrivateKey) key;
            }
        } catch (KeyStoreException | NoSuchAlgorithmException | UnrecoverableKeyException e) {
            log.error("Unable to load private key from keystore");
        }
        throw AuthException.jwtKeystoreError();
    }

    /**
     * Method responsible for registering bean responsible for loading public key from the
     * keystore.
     *
     * @return bean of type {@link RSAPublicKey}
     */
    @Bean
    public RSAPublicKey jwtValidationKey(KeyStore keyStore) {
        try {
            Certificate certificate = keyStore.getCertificate(keyAlias);
            PublicKey publicKey = certificate.getPublicKey();
            if (publicKey instanceof RSAPublicKey) {
                return (RSAPublicKey) publicKey;
            }
        } catch (KeyStoreException e) {
            log.error("Unable to load public key from keystore");
        }
        throw AuthException.jwtKeystoreError();
    }

    /**
     * Method responsible for registering bean responsible for validating JWTs.
     *
     * @return bean of type {@link JwtDecoder}
     */
    @Bean
    public JwtDecoder jwtDecoder(RSAPublicKey publicKey) {
        return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
}
