package io.github.ktg.temm.app.infrastructure;

import io.github.ktg.temm.domain.provider.TokenProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class JwtTokenProvider implements TokenProvider {

    private final String issuer;
    private final Long accessTokenValidity;
    private final Long refreshTokenValidity;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public JwtTokenProvider(
        @Value("${spring.application.name}") String issuer,
        @Value("${jwt.access-token-validity}") Long accessTokenValidity,
        @Value("${jwt.refresh-token-validity}") Long refreshTokenValidity,
        @Value("${jwt.secret-key}") String secretKey) {
        this.issuer = issuer;
        this.accessTokenValidity = accessTokenValidity;
        this.refreshTokenValidity = refreshTokenValidity;
        this.secretKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.jwtParser = Jwts.parser()
            .requireIssuer(issuer)
            .verifyWith(this.secretKey)
            .build();
    }


    @Override
    public String generateAccessToken(String userId) {
        Instant now = Instant.now();
        Instant expired = now.plusMillis(accessTokenValidity);

        return Jwts.builder()
            .subject(userId)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expired))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant expired = now.plusMillis(refreshTokenValidity);

        return Jwts.builder()
            .subject(userId)
            .issuer(issuer)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expired))
            .signWith(secretKey)
            .compact();
    }

    @Override
    public boolean validateAccessToken(String accessToken) {
        try {
            jwtParser.parseSignedClaims(accessToken);
            return true;
        } catch (ExpiredJwtException e) {
            throw e;
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getClass().getSimpleName());
        }
        return false;
    }

    @Override
    public String getUserIdByAccessToken(String accessToken) {
        Jws<Claims> claimsJws = jwtParser.parseSignedClaims(accessToken);
        Claims payload = claimsJws.getPayload();
        return payload.getSubject();
    }
}
