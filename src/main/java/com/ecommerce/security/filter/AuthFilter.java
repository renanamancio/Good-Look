package com.ecommerce.security.filter;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;

/**
 * Filtro JAX-RS responsável pela verificação de autenticação via JWT.
 * Bloqueia o acesso a endpoints protegidos caso o token não esteja presente ou seja inválido.
 */
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    /**
     * Valida a presença do JWT em endpoints que não são públicos.
     *
     * @param requestContext Contexto da requisição.
     * @throws IOException Erro de E/S.
     */
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // ... (resto do método)
        String path = requestContext.getUriInfo().getPath();
        String method = requestContext.getMethod();

        // Endpoints públicos
        if (path.startsWith("api/auth") || 
            (path.equals("api/produtos") && method.equals("GET")) ||
            !path.startsWith("api")) {
            return;
        }

        // Verifica se o token está presente e é válido (o SmallRye JWT já valida a assinatura)
        if (jwt.getName() == null) {
            requestContext.abortWith(
                Response.status(Response.Status.UNAUTHORIZED)
                    .entity("{\"message\": \"Acesso negado: Autenticação necessária\"}")
                    .build()
            );
        }
    }
}
