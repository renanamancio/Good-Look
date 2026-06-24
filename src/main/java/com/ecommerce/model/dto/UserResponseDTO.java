package com.ecommerce.model.dto;

import com.ecommerce.model.entity.Role;
import java.util.UUID;

public record UserResponseDTO(
    UUID id,
    String email,
    Role role
) {}
