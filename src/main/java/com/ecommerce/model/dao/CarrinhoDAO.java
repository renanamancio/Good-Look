package com.ecommerce.model.dao;

import com.ecommerce.model.entity.CarrinhoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class CarrinhoDAO {

    @Inject
    EntityManager em;

    public Optional<CarrinhoEntity> findByUserId(UUID userId) {
        return em.createQuery("SELECT c FROM CarrinhoEntity c WHERE c.user.id = :userId", CarrinhoEntity.class)
                .setParameter("userId", userId)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Transactional
    public void persist(CarrinhoEntity carrinho) {
        em.persist(carrinho);
    }
}
