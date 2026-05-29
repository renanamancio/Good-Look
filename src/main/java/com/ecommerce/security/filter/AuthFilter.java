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

@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthFilter implements ContainerRequestFilter {

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
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
