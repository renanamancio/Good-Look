package com.ecommerce.controller;

import com.ecommerce.model.bo.CarrinhoBO;
import com.ecommerce.model.dto.CarrinhoResponseDTO;
import com.ecommerce.security.exception.BusinessExceptionMapper;
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

import java.util.UUID;

@Path("/api/carrinho")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Carrinho", description = "Operações para gerenciamento do carrinho de compras do cliente")
public class CarrinhoController {

    @Inject
    CarrinhoBO carrinhoBO;

    @Inject
    JsonWebToken jwt;

    @GET
    @Operation(summary = "Obtém o carrinho de compras", description = "Recupera o carrinho de compras do cliente autenticado com seus itens e valores calculados.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Carrinho retornado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = CarrinhoResponseDTO.class)))
    })
    public CarrinhoResponseDTO obterCarrinho() {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        return carrinhoBO.toDTO(carrinhoBO.obterOuCriarCarrinho(userId));
    }

    @POST
    @Path("/itens")
    @Operation(summary = "Adiciona item ao carrinho", description = "Insere um produto ou soma unidades de um produto existente no carrinho do cliente.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Item adicionado com sucesso"),
        @APIResponse(responseCode = "400", description = "Estoque insuficiente ou produto inexistente", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public Response adicionarItem(
            @QueryParam("produtoId") 
            @Parameter(description = "UUID do produto a ser adicionado", required = true) 
            UUID produtoId, 
            @QueryParam("quantidade") 
            @Parameter(description = "Quantidade de unidades", required = true) 
            Integer quantidade) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        carrinhoBO.adicionarItem(userId, produtoId, quantidade);
        return Response.ok().build();
    }

    @DELETE
    @Path("/itens/{produtoId}")
    @Operation(summary = "Remove um item do carrinho", description = "Exclui um produto por completo da sacola do cliente.")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Item removido com sucesso"),
        @APIResponse(responseCode = "400", description = "Erro ao remover item", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public Response removerItem(
            @PathParam("produtoId") 
            @Parameter(description = "UUID do produto a ser excluído do carrinho", required = true) 
            UUID produtoId) {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        carrinhoBO.removerItem(userId, produtoId);
        return Response.noContent().build();
    }

    @DELETE
    @Operation(summary = "Limpa o carrinho", description = "Remove todas as peças do carrinho do cliente.")
    @APIResponses({
        @APIResponse(responseCode = "204", description = "Carrinho limpo com sucesso")
    })
    public Response limparCarrinho() {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        carrinhoBO.limparCarrinho(userId);
        return Response.noContent().build();
    }
}
