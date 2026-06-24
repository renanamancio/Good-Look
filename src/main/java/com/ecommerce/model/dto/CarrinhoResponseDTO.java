package com.ecommerce.model.dto;

import java.util.List;
import java.util.UUID;

public record CarrinhoResponseDTO(
        UUID id,
        Double precoTotal,
        Double quantidadeTotal,
        List<ItemCarrinhoResponseDTO> itens
) {}
