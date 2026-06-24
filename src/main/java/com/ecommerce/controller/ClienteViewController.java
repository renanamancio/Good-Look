package com.ecommerce.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;

import java.util.UUID;

@Path("/cliente")
public class ClienteViewController {

    @Inject
    Template clienteHome; // src/main/resources/templates/clienteHome.html

    @Inject
    Template carrinho; // src/main/resources/templates/carrinho.html

    @Inject
    Template detalhesProduto; // src/main/resources/templates/detalhesProduto.html

    @Inject
    Template listaDesejos; // src/main/resources/templates/listaDesejos.html

    @GET
    @Path("/home")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance home() {
        return clienteHome.instance();
    }

    @GET
    @Path("/carrinho")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance verCarrinho() {
        return carrinho.instance();
    }

    @GET
    @Path("/produto/{id}")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance detalhes(@PathParam("id") UUID id) {
        return detalhesProduto.data("id", id);
    }

    @GET
    @Path("/lista-desejos")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance listaDesejos() {
        return listaDesejos.instance();
    }
}
