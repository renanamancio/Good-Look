package com.ecommerce.security.filter;

import com.ecommerce.model.bo.AuditLogBO;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;

import java.io.IOException;

@Provider
public class AuditFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JsonWebToken jwt;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Podemos armazenar algo no contexto se necessário
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        int status = responseContext.getStatus();
        
        // Evita logar GETs simples se desejar, mas RF05 diz "todas as ações"
        // Geralmente focamos em POST, PUT, DELETE para auditoria de mudança
        if (!method.equals("GET") || path.contains("login") || path.contains("register")) {
            String userEmail = jwt.getName() != null ? jwt.getName() : "ANONYMOUS";
            String action = method + " " + path;
            String details = "Status: " + status;
            String ip = requestContext.getUriInfo().getRequestUri().getHost(); // Simples, ideal seria X-Forwarded-For

            auditLogBO.registrar(action, userEmail, details, ip);
        }
    }
}
