package com.ecommerce.model.dto;

import java.util.UUID;

public record ItemCarrinhoResponseDTO(
        UUID id,
        UUID produtoId,
        String produtoNome,
        Double precoUnitario,
        Integer quantidade
) {}
