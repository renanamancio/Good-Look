package com.ecommerce.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;

@Path("/admin")
public class AdminController {

    @Inject
    Template adminHome; // src/main/resources/templates/adminHome.html

    @GET
    @Path("/home")
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance home() {
        return adminHome.instance();
    }
}
