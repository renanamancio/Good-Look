package com.ecommerce.controller;

import com.ecommerce.model.dto.LoginRequestDTO;
import com.ecommerce.model.dto.LoginResponseDTO;
import com.ecommerce.model.dto.RegisterRequestDTO;
import com.ecommerce.security.AuthService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Cookie;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;

@Path("/api/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthController {

    @Inject
    AuthService authService;

    @POST
    @Path("/login")
    public Response login(LoginRequestDTO request) {
        LoginResponseDTO responseDTO = authService.login(request);
        
        NewCookie cookie = new NewCookie.Builder("jwt")
                .value(responseDTO.token())
                .path("/")
                .maxAge((int) responseDTO.expiresIn().longValue())
                .httpOnly(true)
                .build();

        return Response.ok(responseDTO)
                .cookie(cookie)
                .build();
    }

    @POST
    @Path("/register")
    public Response register(RegisterRequestDTO request) {
        LoginResponseDTO responseDTO = authService.register(request);

        NewCookie cookie = new NewCookie.Builder("jwt")
                .value(responseDTO.token())
                .path("/")
                .maxAge((int) responseDTO.expiresIn().longValue())
                .httpOnly(true)
                .build();

        return Response.ok(responseDTO)
                .cookie(cookie)
                .build();
    }

    @POST
    @Path("/logout")
    public Response logout() {
        NewCookie cookie = new NewCookie.Builder("jwt")
                .value("")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();

        return Response.ok().cookie(cookie).build();
    }
}
