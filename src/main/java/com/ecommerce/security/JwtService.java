package com.ecommerce.security;

import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import com.ecommerce.model.entity.Role;
import java.time.Duration;
import java.util.UUID;

/**
 * Serviço responsável pela geração e gestão de tokens JWT.
 * Utiliza o SmallRye JWT para assinar os tokens.
 */
@ApplicationScoped
public class JwtService {

    @ConfigProperty(name = "mp.jwt.verify.issuer")
    String issuer;

    private static final long EXPIRATION_TIME = 3600;

    /**
     * Gera um token JWT com claims de identificação e role.
     *
     * @param userId ID único do usuário.
     * @param email E-mail do usuário.
     * @param role Perfil de acesso.
     * @return String contendo o token JWT assinado.
     */
    public String generateToken(UUID userId, String email, Role role) {
        return Jwt.issuer(issuer)
                .upn(email)
                .claim("userId", userId.toString())
                .claim("email", email)
                .claim("role", role.name())
                .expiresIn(Duration.ofSeconds(EXPIRATION_TIME))
                .sign();
    }

    /**
     * Retorna o tempo de expiração padrão do token.
     * @return Tempo em segundos.
     */
    public Long getExpirationTime(){
        return EXPIRATION_TIME;
    }
}
