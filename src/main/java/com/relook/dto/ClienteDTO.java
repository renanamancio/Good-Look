package com.relook.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ClienteDTO(
        UUID id,
        String nome,
        String cpf,
        LocalDate dataNascimento,
        String telefone
) {
}
