package com.ecommerce.model.dto;

/**
 * DTO para atualização de dados do usuário.
 */
public record UserUpdateDTO(
    String email,
    String senha
) {}
