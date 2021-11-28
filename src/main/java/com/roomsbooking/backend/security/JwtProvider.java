package com.roomsbooking.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Date;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Class responsible for handling operations connected with JWT.
 */
@Service
@Getter
@RequiredArgsConstructor
public class JwtProvider {

    private final RSAPrivateKey privateKey;
    private final RSAPublicKey publicKey;
    @Value("${security.jwt.expiration.time}")
    private Long jwtExpirationInMillis;

    /**
     * Method responsible for generating JWT for a user.
     *
     * @param email email of specific user
     * @return generated JWT
     */
    public String generateToken(String email) {
        Date expirationTime = Date.from(Instant.now().plusMillis(jwtExpirationInMillis));
        return Jwts.builder()
            .setSubject(email)
            .signWith(privateKey)
            .setExpiration(expirationTime)
            .compact();
    }

    /**
     * Method responsible for getting user's email from a token.
     *
     * @param token user's JWT
     * @return email of a specific user
     */
    public String getEmailFromJwt(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(publicKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getSubject();
    }

    /**
     * Method responsible for validating a token.
     *
     * @param token user's JWT
     * @return true if token is valid else false
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(publicKey)
                .build()
                .parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }
}
