package com.ecommerce.model.dao;

import com.ecommerce.model.entity.ItemCarrinhoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ItemCarrinhoDAO {

    @Inject
    EntityManager em;

    public Optional<ItemCarrinhoEntity> findById(UUID id) {
        return Optional.ofNullable(em.find(ItemCarrinhoEntity.class, id));
    }

    public List<ItemCarrinhoEntity> findByCarrinhoId(UUID carrinhoId) {
        return em.createQuery("SELECT i FROM ItemCarrinhoEntity i WHERE i.carrinho.id = :carrinhoId", ItemCarrinhoEntity.class)
                .setParameter("carrinhoId", carrinhoId)
                .getResultList();
    }

    @Transactional
    public void persist(ItemCarrinhoEntity item) {
        em.persist(item);
    }

    @Transactional
    public void merge(ItemCarrinhoEntity item) {
        em.merge(item);
    }

    @Transactional
    public void delete(ItemCarrinhoEntity item) {
        em.remove(em.contains(item) ? item : em.merge(item));
    }

    @Transactional
    public void deleteByCarrinhoId(UUID carrinhoId) {
        em.createQuery("DELETE FROM ItemCarrinhoEntity i WHERE i.carrinho.id = :carrinhoId")
                .setParameter("carrinhoId", carrinhoId)
                .executeUpdate();
    }
}
