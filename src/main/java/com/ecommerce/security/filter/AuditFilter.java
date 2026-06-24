package com.ecommerce.security.filter;

import com.ecommerce.model.bo.AuditLogBO;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.context.ManagedExecutor;

import java.io.IOException;

/**
 * Filtro JAX-RS para auditoria de requisições e respostas.
 * Registra as ações dos usuários no banco de dados para fins de rastreabilidade (RF05).
 */
@Provider
public class AuditFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JsonWebToken jwt;

    @Inject
    ManagedExecutor managedExecutor;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Nada a fazer no pré-processamento
    }

    /**
     * Captura os dados da resposta e registra no log de auditoria de forma assíncrona.
     */
    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {

        String method = requestContext.getMethod();
        String path = requestContext.getUriInfo().getPath();
        int status = responseContext.getStatus();

        if (!method.equals("GET") || path.contains("login") || path.contains("register")) {
            String userEmail = jwt.getName() != null ? jwt.getName() : "ANONYMOUS";
            String action = method + " " + path;
            String details = "Status: " + status;
            String ip = requestContext.getUriInfo().getRequestUri().getHost();
            
            // Desvia a execução bloqueante de persistência para uma thread worker gerenciada
            managedExecutor.runAsync(() -> {
                try {
                    auditLogBO.registrar(action, userEmail, details, ip);
                } catch (Exception e) {
                    // Evita que falhas na auditoria parem a resposta do usuário
                    System.err.println("Erro ao salvar log de auditoria: " + e.getMessage());
                }
            });
        }
    }
}
