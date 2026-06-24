package com.ecommerce.controller;

import com.ecommerce.model.bo.UserBO;
import com.ecommerce.model.dto.UserResponseDTO;
import com.ecommerce.model.dto.UserUpdateDTO;
import com.ecommerce.model.entity.Role;
import com.ecommerce.security.exception.BusinessExceptionMapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

import java.util.List;
import java.util.UUID;

/**
 * Controller híbrido para gestão de usuários.
 * Renderiza templates Qute e provê APIs JSON.
 */
@Path("/admin/usuarios")
@Tag(name = "Usuários", description = "Operações administrativas de gerenciamento de usuários")
public class UserController {

    @Inject
    UserBO userBO;

    @Inject
    JsonWebToken jwt;

    @Inject
    Template usuarios; // Injeta src/main/resources/templates/usuarios.html

    @Inject
    Template cadastrarUsuario; // src/main/resources/templates/cadastrarUsuario.html

    /**
     * Renderiza a página de cadastro de usuários (SSR).
     */
    @GET
    @Path("/cadastrar")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance cadastrarPagina() {
        return cadastrarUsuario.instance();
    }

    /**
     * Renderiza a página de listagem de usuários (SSR).
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance listarPagina() {
        return usuarios.data("listaUsuarios", userBO.listarTodos());
    }

    /**
     * API JSON para listagem dinâmica.
     */
    @GET
    @Path("/api")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todos os usuários", description = "Retorna uma lista com todos os usuários cadastrados no sistema. Acesso restrito.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Lista obtida com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserResponseDTO.class)))
    })
    public List<UserResponseDTO> listar() {
        return userBO.listarTodos();
    }

    @GET
    @Path("/api/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Busca usuário por ID", description = "Obtém os detalhes de um usuário específico informando o seu ID.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Usuário localizado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Usuário não encontrado", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public UserResponseDTO buscar(
            @PathParam("id") 
            @Parameter(description = "Identificador único (UUID) do usuário", required = true) 
            UUID id) {
        return userBO.buscarPorId(id);
    }

    @PUT
    @Path("/api/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Atualiza um usuário", description = "Modifica os dados (e-mail ou senha) de um usuário existente.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Usuário atualizado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = UserResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Dados inválidos, e-mail já em uso ou usuário não encontrado", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public UserResponseDTO atualizar(
            @PathParam("id") 
            @Parameter(description = "UUID do usuário a ser atualizado", required = true) 
            UUID id, 
            UserUpdateDTO dto) {
        String emailExecutor = jwt.getName();
        String roleStr = jwt.getClaim("role");
        Role roleExecutor = roleStr != null ? Role.valueOf(roleStr) : Role.USER;
        return userBO.atualizar(id, dto, emailExecutor, roleExecutor);
    }

    @DELETE
    @Path("/api/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Exclui um usuário", description = "Remove permanentemente um usuário do banco de dados.")
    @APIResponses({
        @APIResponse(responseCode = "240", description = "Usuário excluído com sucesso (No Content)"),
        @APIResponse(responseCode = "204", description = "Usuário removido com sucesso"),
        @APIResponse(responseCode = "400", description = "Usuário não encontrado ou erro de permissão", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public Response deletar(
            @PathParam("id") 
            @Parameter(description = "UUID do usuário a ser removido", required = true) 
            UUID id) {
        String emailExecutor = jwt.getName();
        String roleStr = jwt.getClaim("role");
        Role roleExecutor = roleStr != null ? Role.valueOf(roleStr) : Role.USER;
        userBO.excluir(id, emailExecutor, roleExecutor);
        return Response.noContent().build();
    }
}
