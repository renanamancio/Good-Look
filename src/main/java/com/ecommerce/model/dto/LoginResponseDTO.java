package com.ecommerce.model.dto;

import com.ecommerce.model.entity.Role;

public record LoginResponseDTO(
        String token,
        String email,
        Role autorizacao,
        Long expiresIn
) {
}
