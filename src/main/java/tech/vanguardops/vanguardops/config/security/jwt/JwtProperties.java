package tech.vanguardops.vanguardops.config.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

/**
 * Properties for JWT configuration.
 * @param secret the secret key used for signing JWTs
 * @param issuer the issuer of the JWTs
 * @param expiresInMs the expiration time in milliseconds
 */
@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String secret,
        String issuer,
        Long expiresInMs
) {

    /**
     * Get the secret key as a SecretKey object.
     * @return the secret key
     */
    public SecretKey getSecretKey() {
        return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}