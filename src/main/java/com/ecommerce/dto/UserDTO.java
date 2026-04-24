package com.ecommerce.dto;

import com.ecommerce.entity.Role;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String senha,
        Role autorizacao
) {
}