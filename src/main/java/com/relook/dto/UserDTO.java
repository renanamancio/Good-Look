package com.relook.dto;

import com.relook.entity.Role;

import java.util.UUID;

public record UserDTO(
        UUID id,
        String email,
        String senha,
        Role autorizacao
) {
}
