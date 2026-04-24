package com.ecommerce.dto;

import com.ecommerce.entity.Role;

public record LoginResponseDTO(
        String token,
        String email,
        Role autorizacao,
        Long expiresIn
) {
}
