package com.ecommerce.model.bo;

import com.ecommerce.model.dao.AuditLogRepository;
import com.ecommerce.model.entity.AuditLogEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class AuditLogBO {

    @Inject
    AuditLogRepository auditLogRepository;

    @Transactional
    public void registrar(String acao, String usuarioEmail, String detalhes, String ipAddress) {
        AuditLogEntity log = new AuditLogEntity(acao, usuarioEmail, detalhes, ipAddress);
        auditLogRepository.persist(log);
    }

    public List<AuditLogEntity> buscarPorUsuario(String usuarioEmail) {
        return auditLogRepository.findByUsuario(usuarioEmail);
    }

    public List<AuditLogEntity> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return auditLogRepository.findByPeriodo(inicio, fim);
    }

    public List<AuditLogEntity> listarTodos() {
        return auditLogRepository.listAll();
    }
}
