package com.ecommerce.dto;

import java.util.UUID;

public record EnderecoDTO(
        UUID id,
        String logradouro,
        String numero,
        String complemento,
        String bairro,
        String cidade,
        String uf,
        String cep
) {
}