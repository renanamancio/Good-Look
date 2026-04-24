package com.ecommerce.bo;

import com.ecommerce.dao.AuditLogRepository;
import com.ecommerce.entity.AuditLog;
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
        AuditLog log = new AuditLog(acao, usuarioEmail, detalhes, ipAddress);
        auditLogRepository.persist(log);
    }

    public List<AuditLog> buscarPorUsuario(String usuarioEmail) {
        return auditLogRepository.findByUsuario(usuarioEmail);
    }

    public List<AuditLog> buscarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return auditLogRepository.findByPeriodo(inicio, fim);
    }

    public List<AuditLog> listarTodos() {
        return auditLogRepository.listAll();
    }
}
