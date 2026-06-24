package com.ecommerce.model.dao;

import com.ecommerce.model.entity.PedidoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class PedidoDAO {

    @Inject
    EntityManager em;

    public Optional<PedidoEntity> findByIdOptional(UUID id) {
        return Optional.ofNullable(em.find(PedidoEntity.class, id));
    }

    public List<PedidoEntity> findByUserId(UUID userId) {
        return em.createQuery("SELECT p FROM PedidoEntity p WHERE p.user.id = :userId", PedidoEntity.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Transactional
    public void persist(PedidoEntity entity) {
        em.persist(entity);
    }
}
