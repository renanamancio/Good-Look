package com.ecommerce.controller;

import com.ecommerce.model.dto.LoginRequestDTO;
import com.ecommerce.model.dto.LoginResponseDTO;
import com.ecommerce.model.dto.RegisterRequestDTO;
import com.ecommerce.security.AuthService;
import com.ecommerce.security.exception.BusinessExceptionMapper;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.*;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;

/**
 * Controller responsável pelas operações de autenticação e registro de usuários.
 * Fornece endpoints para login, registro e logout, gerenciando cookies JWT.
 */
@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Autenticação", description = "Operações relacionadas à autenticação e registro de usuários")
public class AuthController {

    @Inject
    AuthService authService;

    @Inject
    Template cadastro; // Injeta src/main/resources/templates/cadastro.html

    /**
     * Renderiza a página de cadastro (SSR).
     */
    @GET
    @Path("/cadastro")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance exibirCadastro() {
        return cadastro.instance();
    }

    @POST
    @Path("/login")
    @Operation(summary = "Realiza o login do usuário", description = "Autentica um usuário usando e-mail e senha, retornando um token JWT de acesso.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Login efetuado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LoginResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "E-mail ou senha inválidos", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public Response login(LoginRequestDTO request) {
        LoginResponseDTO responseDTO = authService.login(request);

        NewCookie cookie = createAuthCookie(responseDTO.token(), 60 * 60 * 8); // 8 horas

        return Response.ok(responseDTO)
                .cookie(cookie)
                .build();
    }

    @POST
    @Path("/register")
    @Operation(summary = "Registra um novo usuário", description = "Cadastra um novo usuário e cliente na plataforma, retornando um token JWT de acesso.")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Usuário registrado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = LoginResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Erro na validação dos dados ou conflito de e-mail/CPF", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public Response register(RegisterRequestDTO request) {
        LoginResponseDTO responseDTO = authService.register(request);

        NewCookie cookie = createAuthCookie(responseDTO.token(), 60 * 60 * 8);

        return Response.status(Response.Status.CREATED)
                .entity(responseDTO)
                .cookie(cookie)
                .build();
    }

    @POST
    @Path("/logout")
    @Operation(summary = "Realiza o logout do usuário", description = "Limpa os cookies de sessão de autenticação do usuário.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Logout efetuado com sucesso")
    })
    public Response logout() {
        NewCookie cookieVazio = createAuthCookie("", 0);
        return Response.ok().cookie(cookieVazio).build();
    }

    private NewCookie createAuthCookie(String token, int maxAge) {
        return new NewCookie.Builder("AUTH_TOKEN")
                .value(token)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(false) // false para desenvolvimento (HTTP)
                .sameSite(NewCookie.SameSite.STRICT)
                .build();
    }
}
