package com.samanthamaiaduarte.todoapi.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.samanthamaiaduarte.todoapi.domain.user.LoginResponseDTO;
import com.samanthamaiaduarte.todoapi.domain.user.User;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenCreationException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenExpiredException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenInvalidArgsException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenInvalidException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    public LoginResponseDTO generateToken(User user) {

        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            LocalDateTime refresh = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);

            String token = JWT.create()
                    .withIssuer("todoapi")
                    .withSubject(user.getLogin())
                    .withExpiresAt(generateExpirationDate(refresh))
                    .sign(algorithm);

            return new LoginResponseDTO(refresh, "bearer", token, 7200);
        }
        catch (IllegalArgumentException exception) {
            throw new ApiTokenInvalidArgsException("Invalid information: " + exception.getMessage());
        }
        catch (JWTCreationException exception) {
            throw new ApiTokenCreationException("Error generating token: " + exception.getMessage());
        }
    }

    public String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            return JWT.require(algorithm)
                    .withIssuer("todoapi")
                    .build()
                    .verify(token.trim())
                    .getSubject();
        }
        catch (TokenExpiredException exception) {
            throw new ApiTokenExpiredException("Token has expired at " + exception.getExpiredOn().truncatedTo(ChronoUnit.SECONDS));
        }
        catch (JWTVerificationException exception) {
            throw new ApiTokenInvalidException("Invalid token.");
        }
    }

    private Instant generateExpirationDate(LocalDateTime refresh) {
        return refresh.plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }
}
