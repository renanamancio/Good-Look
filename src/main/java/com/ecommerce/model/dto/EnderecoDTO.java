package com.ecommerce.model.dto;

import java.util.UUID;

public record EnderecoDTO(
        UUID id,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String cep,
        String quadra,
        String lote
) {
}