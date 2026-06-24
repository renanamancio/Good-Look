package com.ecommerce.controller;

import com.ecommerce.model.bo.PedidoBO;
import com.ecommerce.model.dto.PedidoResponseDTO;
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

import java.util.List;
import java.util.UUID;

@Path("/api/pedidos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Pedidos", description = "Operações de processamento de checkout e consulta de pedidos")
public class PedidoController {

    @Inject
    PedidoBO pedidoBO;

    @Inject
    JsonWebToken jwt;

    @POST
    @Path("/checkout")
    @Operation(summary = "Finaliza a compra", description = "Processa o checkout dos produtos contidos no carrinho, realizando a baixa do estoque e registrando o pedido.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Pedido finalizado e faturado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PedidoResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Carrinho vazio ou estoque insuficiente", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public PedidoResponseDTO finalizarCompra() {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        return pedidoBO.finalizarPedido(userId);
    }

    @GET
    @Operation(summary = "Lista os pedidos do cliente", description = "Retorna o histórico de todos os pedidos finalizados pelo cliente autenticado.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Pedidos listados com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PedidoResponseDTO.class)))
    })
    public List<PedidoResponseDTO> listarMeusPedidos() {
        UUID userId = UUID.fromString(jwt.getClaim("userId"));
        return pedidoBO.listarPorUsuario(userId);
    }

    @GET
    @Path("/{id}")
    @Operation(summary = "Busca pedido por ID", description = "Obtém as informações de um pedido específico com sua data, status, valor e lista de itens.")
    @APIResponses({
        @APIResponse(responseCode = "200", description = "Pedido localizado com sucesso", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = PedidoResponseDTO.class))),
        @APIResponse(responseCode = "400", description = "Pedido não encontrado", 
                     content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(implementation = BusinessExceptionMapper.ErrorResponse.class)))
    })
    public PedidoResponseDTO buscarPedido(
            @PathParam("id") 
            @Parameter(description = "UUID do pedido", required = true) 
            UUID id) {
        return pedidoBO.buscarPorId(id);
    }
}
