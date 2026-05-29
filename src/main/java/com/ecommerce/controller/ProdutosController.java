package com.ecommerce.controller;

import com.ecommerce.model.bo.ProdutosBO;
import com.ecommerce.model.dto.ProdutosDTO;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.UUID;

@Path("/api/produtos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ProdutosController {

    @Inject
    ProdutosBO produtosBO;

    @GET
    public List<ProdutosDTO> listar() {
        return produtosBO.listarTodos();
    }

    @GET
    @Path("/{id}")
    public ProdutosDTO buscar(@PathParam("id") UUID id) {
        return produtosBO.buscarPorId(id);
    }

    @POST
    public Response criar(ProdutosDTO dto) {
        ProdutosDTO criado = produtosBO.criar(dto);
        return Response.status(Response.Status.CREATED).entity(criado).build();
    }

    @PUT
    @Path("/{id}")
    public ProdutosDTO atualizar(@PathParam("id") UUID id, ProdutosDTO dto) {
        return produtosBO.atualizar(id, dto);
    }

    @DELETE
    @Path("/{id}")
    public Response deletar(@PathParam("id") UUID id) {
        produtosBO.deletar(id);
        return Response.noContent().build();
    }
}
