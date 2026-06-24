package com.ecommerce.model.dao;

import com.ecommerce.model.entity.AuditLogEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class AuditLogDAO {

    @Inject
    EntityManager em;

    @Transactional
    public void persist(AuditLogEntity log) {
        em.persist(log);
    }

    public List<AuditLogEntity> listAll() {
        return em.createQuery("SELECT a FROM AuditLogEntity a", AuditLogEntity.class).getResultList();
    }

    public List<AuditLogEntity> findByUsuario(String usuarioEmail){
        return em.createQuery("SELECT a FROM AuditLogEntity a WHERE a.usuarioEmail = :email", AuditLogEntity.class)
                .setParameter("email", usuarioEmail)
                .getResultList();
    }

    public List<AuditLogEntity> findByPeriodo(LocalDateTime inicio, LocalDateTime fim){
        return em.createQuery("SELECT a FROM AuditLogEntity a WHERE a.dataHora >= :inicio AND a.dataHora <= :fim", AuditLogEntity.class)
                .setParameter("inicio", inicio)
                .setParameter("fim", fim)
                .getResultList();
    }
}
