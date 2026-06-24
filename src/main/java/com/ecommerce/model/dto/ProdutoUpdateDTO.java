package com.ecommerce.model.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record ProdutoUpdateDTO(
    String nome,
    String descricao,
    @PositiveOrZero(message = "Preço deve ser maior ou igual a zero")
    Double preco,
    @PositiveOrZero(message = "Quantidade deve ser maior ou igual a zero")
    Integer quantidade,
    String imagem,
    String url
) {}
