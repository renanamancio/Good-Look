package com.ecommerce.security;

import com.ecommerce.model.entity.Role;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import java.time.Duration;
import java.util.UUID;

@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issue")
    String issuer;

    private static final long EXPIRATION_TIME = 3600;

    public String generateToken(UUID userId, String email, Role role) {
        return Jwt.issuer(issuer)
                .upn(email)
                .claim("userId", userId.toString())
                .claim("email", email)
                .claim("role", role.name())
                .expiresIn(Duration.ofSeconds(EXPIRATION_TIME))
                .sign();
    }

    public Long getExpirationTime(){
        return EXPIRATION_TIME;
    }
}
