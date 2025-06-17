package com.samanthamaiaduarte.todoapi.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.samanthamaiaduarte.todoapi.domain.user.LoginResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public LoginResponseDTO generateToken(User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            LocalDateTime refresh = LocalDateTime.now();

            String token = JWT.create()
                    .withIssuer("todoapi")
                    .withSubject(user.getLogin())
                    .withExpiresAt(generateExpirationDate(refresh))
                    .sign(algorithm);

            return new LoginResponseDTO(refresh, "bearer", token, 7200);
        } catch (JWTCreationException exception) {
            throw new RuntimeException("Error generating token: " + exception);
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("todoapi")
                    .build()
                    .verify(token)
                    .getSubject();
        } catch (JWTVerificationException exception) {
            return "";
        }
    }

    private Instant generateExpirationDate(LocalDateTime refresh) {
        return refresh.plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
