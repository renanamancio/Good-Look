package com.ecommerce.model.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record PedidoResponseDTO(
        UUID id,
        LocalDateTime dataPedido,
        Double valorTotal,
        String status,
        List<ItemPedidoResponseDTO> itens
) {}
