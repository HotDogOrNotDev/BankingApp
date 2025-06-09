package com.aston.frontendpracticeservice.service.impl;

import com.aston.frontendpracticeservice.domain.entity.User;
import com.aston.frontendpracticeservice.security.JwtAuthentication;
import com.aston.frontendpracticeservice.security.Role;
import com.aston.frontendpracticeservice.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${auth.access_token_life_seconds}")
    private Long accessTokenLifeInSeconds;

    @Value("${auth.refresh_token_life_seconds}")
    private Long refreshTokenLifeInSeconds;

    @Override
    public String generateAccessToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();

        final Instant accessExpirationInstant =
                now.plusSeconds(accessTokenLifeInSeconds)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

        final Date accessExpiration = Date.from(accessExpirationInstant);

        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(accessExpiration)
                .signWith(getSignKey())
                .claim("roles", extractRoles(user))
                .claim("login", user.getLogin())
                .compact();
    }

    @Override
    public Set<String> extractRoles(User user) {
        return user.getUserRoles().stream()
                .map(ur -> ur.getRole().name())
                .collect(Collectors.toSet());
    }

    @Override
    public String generateRefreshToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();

        final Instant refreshExpirationInstant =
                now.plusSeconds(refreshTokenLifeInSeconds)
                        .atZone(ZoneId.systemDefault())
                        .toInstant();

        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(getSignKey())
                .compact();
    }

    @Override
    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken);
    }

    @Override
    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken);
    }

    @Override
    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token);
    }

    @Override
    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token);
    }

    @Override
    public Claims getClaims(@NonNull String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build().parseClaimsJws(token)
                .getBody();
    }

    @Override
    public boolean validateToken(@NonNull String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSignKey())
                    .build().parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException
                 | UnsupportedJwtException
                 | MalformedJwtException
                 | SignatureException e) {
            throw new JwtException("Incorrect jwt token");
        }
    }

    @Override
    public Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public JwtAuthentication generate(Claims claims) {
        String login = claims.getSubject();
        Role role = Role.valueOf(claims.get("roles", String.class));
        new JwtAuthentication(true, login, Set.of(role));
        return null;
    }

}

