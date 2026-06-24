package com.ecommerce.controller;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;

/**
 * Controller para a Landing Page (Login).
 */
@Path("/")
public class IndexController {

    @Inject
    Template index;

    @GET
    @Produces(MediaType.TEXT_HTML)
    @Operation(hidden = true)
    public TemplateInstance get() {
        return index.instance();
    }
}
