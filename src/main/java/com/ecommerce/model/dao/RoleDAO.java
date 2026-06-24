package com.ecommerce.model.dao;

import com.ecommerce.model.entity.Role;
import com.ecommerce.model.entity.RoleEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RoleDAO {

    @Inject
    EntityManager em;

    public Optional<RoleEntity> findByNome(Role nome) {
        return em.createQuery("SELECT r FROM RoleEntity r WHERE r.nome = :nome", RoleEntity.class)
                .setParameter("nome", nome)
                .getResultList()
                .stream()
                .findFirst();
    }
}
