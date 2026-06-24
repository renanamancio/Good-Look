package com.ecommerce.controller;

import com.ecommerce.model.bo.AuditLogBO;
import com.ecommerce.model.dto.AuditLogResponseDTO;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;

/**
 * Controller para consulta de logs de auditoria e relatórios.
 * Renderiza templates via Qute e provê endpoints JSON.
 */
@Path("/admin/relatorios")
@Tag(name = "Auditoria", description = "Operações de consulta aos logs de auditoria e relatórios do sistema")
public class AuditLogController {

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    Template relatorios; // Injeta src/main/resources/templates/relatorios.html

    /**
     * Renderiza a página de relatórios (SSR).
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance exibirRelatorios() {
        return relatorios.data("logs", auditLogBO.listarTodos());
    }

    /**
     * Retorna a lista completa de logs do sistema em JSON.
     */
    @GET
    @Path("/api")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todos os logs de auditoria", description = "Retorna o histórico completo com todas as ações efetuadas, usuários executores, IP de origem e carimbo de data/hora (logs de auditoria).")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Logs de auditoria retornados com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = AuditLogResponseDTO.class)))
    })
    public List<AuditLogResponseDTO> listar() {
        return auditLogBO.listarTodos();
    }
}
