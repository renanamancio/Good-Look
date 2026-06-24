package com.ecommerce.model.dao;

import com.ecommerce.model.entity.ItemPedidoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ItemPedidoDAO {

    @Inject
    EntityManager em;

    public Optional<ItemPedidoEntity> findById(UUID id) {
        return Optional.ofNullable(em.find(ItemPedidoEntity.class, id));
    }

    public List<ItemPedidoEntity> findByPedidoId(UUID pedidoId) {
        return em.createQuery("SELECT i FROM ItemPedidoEntity i WHERE i.pedido.id = :pedidoId", ItemPedidoEntity.class)
                .setParameter("pedidoId", pedidoId)
                .getResultList();
    }

    @Transactional
    public void persist(ItemPedidoEntity item) {
        em.persist(item);
    }
}
