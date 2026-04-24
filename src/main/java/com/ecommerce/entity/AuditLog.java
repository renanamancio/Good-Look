package com.ecommerce.entity;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuditLog {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column (nullable = false)
    private String acao;

    private String usuarioEmail;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    private String detalhes;

    @Column(length = 45)
    private String ipAddress;

    public AuditLog(String acao, String usuarioEmail, String detalhes, String ipAddress) {
        this.acao = acao;
        this.usuarioEmail = usuarioEmail;
        this.dataHora = LocalDateTime.now();
        this.detalhes = detalhes;
        this.ipAddress = ipAddress;
    }
}
