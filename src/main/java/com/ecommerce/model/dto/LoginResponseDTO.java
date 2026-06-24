package com.ecommerce.model.dto;

/**
 * Resposta de login contendo o token JWT e os dados do usuário.
 */
public record LoginResponseDTO(
        String token,
        UserResponseDTO user,
        Long expiresIn
) {
}
