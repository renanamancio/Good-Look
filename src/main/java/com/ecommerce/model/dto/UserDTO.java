package com.ecommerce.model.dto;

import com.ecommerce.model.entity.Role;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String senha,
        Role autorizacao
) {
}