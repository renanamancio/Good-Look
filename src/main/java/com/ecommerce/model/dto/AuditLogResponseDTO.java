package com.ecommerce.model.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponseDTO(
    UUID id,
    String acao,
    String usuarioEmail,
    LocalDateTime dataHora,
    String detalhes,
    String ipAddress
) {}
