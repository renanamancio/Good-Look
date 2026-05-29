package com.ecommerce.model.dao;

import com.ecommerce.model.entity.AuditLogEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AuditLogRepository implements PanacheRepositoryBase<AuditLogEntity, UUID> {
    public List<AuditLogEntity> findByUsuario(String usuarioEmail){
        return list("usuarioEmail", usuarioEmail);
    }
    public List<AuditLogEntity> findByPeriodo(LocalDateTime inicio, LocalDateTime fim){
        return list("datahora >= ?1 and dataHora <= ?2", inicio, fim);
    }
}
