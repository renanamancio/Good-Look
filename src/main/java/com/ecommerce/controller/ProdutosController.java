package com.ecommerce.controller;

import com.ecommerce.model.bo.ProdutosBO;
import com.ecommerce.model.dto.ProdutoCreateDTO;
import com.ecommerce.model.dto.ProdutoResponseDTO;
import com.ecommerce.model.dto.ProdutoUpdateDTO;
import com.ecommerce.model.entity.Role;
import com.ecommerce.security.exception.BusinessExceptionMapper;
import com.ecommerce.security.exception.UnauthorizedException;
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

import org.jboss.resteasy.reactive.RestForm;
import org.jboss.resteasy.reactive.multipart.FileUpload;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import com.ecommerce.security.exception.BusinessException;

/**
 * Controller híbrido para gestão de produtos.
 * Renderiza templates via Qute (SSR) e provê endpoints JSON para Fetch API.
 */
@Path("/admin/produtos")
@Tag(name = "Produtos", description = "Operações administrativas para gerenciamento do catálogo de produtos")
public class ProdutosController {

    @Inject
    ProdutosBO produtosBO;

    @Inject
    Template produtos; // Injeta src/main/resources/templates/produtos.html

    @Inject
    JsonWebToken jwt;

    @Inject
    Template cadastrarProduto; // src/main/resources/templates/cadastrarProduto.html

    /**
     * Renderiza a página de cadastro de produtos (SSR).
     */
    @GET
    @Path("/cadastrar")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance cadastrarPagina() {
        return cadastrarProduto.instance();
    }

    /**
     * Renderização Inicial via Qute (Server-Side).
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance listarPagina() {
        return produtos.data("listaProdutos", produtosBO.listarTodos());
    }

    /**
     * Endpoint API para listagem dinâmica via Fetch.
     */
    @GET
    @Path("/api")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todos os produtos", description = "Retorna uma lista com todas as peças e produtos ativos no acervo.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Produtos listados com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdutoResponseDTO.class)))
    })
    public List<ProdutoResponseDTO> listarJSON() {
        return produtosBO.listarTodos();
    }

    @GET
    @Path("/api/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Busca produto por ID", description = "Obtém as informações detalhadas de uma peça específica pelo seu ID.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Produto localizado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdutoResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Produto não encontrado", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public ProdutoResponseDTO buscar(
            @PathParam("id") 
            @Parameter(description = "UUID do produto", required = true) 
            UUID id) {
        return produtosBO.buscarPorId(id);
    }

    @POST
    @Path("/api")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Cria um novo produto com imagem", description = "Adiciona uma nova peça ao catálogo de produtos com upload de imagem. Apenas Administradores.")
    @APIResponses({
        @APIResponse(responseCode = "201", description = "Produto cadastrado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdutoResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Valores inválidos ou URL amigável já cadastrada", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class))),
        @APIResponse(responseCode = "401", description = "Acesso negado: Somente administradores")
    })
    public Response criar(
            @RestForm("nome") String nome,
            @RestForm("descricao") String descricao,
            @RestForm("preco") Double preco,
            @RestForm("quantidade") Integer quantidade,
            @RestForm("url") String url,
            @RestForm("imagem") FileUpload file) {
        validarAdmin();

        if (nome == null || nome.trim().isEmpty()) {
            throw new BusinessException("Nome é obrigatório");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new BusinessException("Descrição é obrigatória");
        }
        if (preco == null || preco < 0) {
            throw new BusinessException("Preço deve ser maior ou igual a zero");
        }
        if (quantidade == null || quantidade < 0) {
            throw new BusinessException("Quantidade deve ser maior ou igual a zero");
        }
        if (url == null || url.trim().isEmpty()) {
            throw new BusinessException("URL amigável é obrigatória");
        }
        if (file == null) {
            throw new BusinessException("A fotografia editorial é obrigatória");
        }

        String imageUrl = salvarImagem(file);

        ProdutoCreateDTO dto = new ProdutoCreateDTO(nome, descricao, preco, quantidade, imageUrl, url);
        ProdutoResponseDTO criado = produtosBO.criar(dto);
        return Response.status(Response.Status.CREATED).entity(criado).build();
    }

    @PUT
    @Path("/api/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Atualiza um produto", description = "Modifica os dados de uma peça existente no catálogo. Apenas Administradores.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Produto atualizado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = ProdutoResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Dados inválidos ou produto não localizado", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class))),
        @APIResponse(responseCode = "401", description = "Acesso negado: Somente administradores")
    })
    public ProdutoResponseDTO atualizar(
            @PathParam("id") 
            @Parameter(description = "UUID do produto a ser editado", required = true) 
            UUID id, 
            ProdutoUpdateDTO dto) {
        validarAdmin();
        return produtosBO.atualizar(id, dto);
    }

    @DELETE
    @Path("/api/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletar(@PathParam("id") UUID id) {
        validarAdmin();
        produtosBO.deletar(id);
        return Response.noContent().build();
    }

    private void validarAdmin() {
        String roleStr = jwt.getClaim("role");
        if (roleStr == null || !Role.ADMIN.name().equals(roleStr)) {
            throw new UnauthorizedException("Acesso negado: Somente administradores podem realizar esta ação");
        }
    }

    private String salvarImagem(FileUpload file) {
        String contentType = file.contentType();
        if (contentType == null || (!contentType.equals("image/jpeg") && !contentType.equals("image/png") && !contentType.equals("image/jpg"))) {
            throw new BusinessException("Apenas imagens JPEG ou PNG são permitidas");
        }

        try {
            String originalName = file.fileName();
            String extension = originalName.contains(".") ? originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String fileName = UUID.randomUUID().toString() + extension;

            // Salva no código fonte (src/main/resources/META-INF/resources/uploads)
            java.nio.file.Path sourceDir = Paths.get("src/main/resources/META-INF/resources/uploads");
            if (!Files.exists(sourceDir)) {
                Files.createDirectories(sourceDir);
            }
            java.nio.file.Path targetSourceFile = sourceDir.resolve(fileName);
            Files.copy(file.uploadedFile(), targetSourceFile, StandardCopyOption.REPLACE_EXISTING);

            // Salva nas classes compiladas (target/classes/META-INF/resources/uploads)
            java.nio.file.Path classesDir = Paths.get("target/classes/META-INF/resources/uploads");
            if (!Files.exists(classesDir)) {
                Files.createDirectories(classesDir);
            }
            java.nio.file.Path targetClassFile = classesDir.resolve(fileName);
            Files.copy(file.uploadedFile(), targetClassFile, StandardCopyOption.REPLACE_EXISTING);

            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new BusinessException("Falha ao salvar a imagem: " + e.getMessage());
        }
    }
}
