package com.ecommerce.model.dto;

import java.util.UUID;

public record ProdutosDTO(
    UUID id,
    String nome,
    String descricao,
    Double preco,
    Integer quantidade,
    String imagem,
    String url
) {}
