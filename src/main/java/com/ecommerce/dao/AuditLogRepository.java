package com.ecommerce.dao;

import com.ecommerce.entity.AuditLog;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AuditLogRepository implements PanacheRepositoryBase<AuditLog, UUID> {
    public List<AuditLog> findByUsuario(String usuarioEmail){
        return list("usuarioEmail", usuarioEmail);
    }
    public List<AuditLog> findByPeriodo(LocalDateTime inicio, LocalDateTime fim){
        return list("datahora >= ?1 and dataHora <= ?2", inicio, fim);
    }
}
