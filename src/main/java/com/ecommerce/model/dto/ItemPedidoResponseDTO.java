package com.ecommerce.model.dto;

import java.util.UUID;

public record ItemPedidoResponseDTO(
        UUID id,
        UUID produtoId,
        String produtoNome,
        Integer quantidade,
        Double precoUnitario,
        Double precoTotal
) {}
