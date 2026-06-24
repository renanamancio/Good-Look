package com.ecommerce.model.dao;

import com.ecommerce.model.entity.UserEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class UserDAO {

    @Inject
    EntityManager em;

    public Optional<UserEntity> findById(UUID id) {
        return Optional.ofNullable(em.find(UserEntity.class, id));
    }

    public Optional<UserEntity> findByEmail(String email) {
        return em.createQuery("SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", email)
                .getResultList()
                .stream()
                .findFirst();
    }

    public boolean existsByEmail(String email) {
        Long count = em.createQuery("SELECT COUNT(u) FROM UserEntity u WHERE u.email = :email", Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count > 0;
    }

    public List<UserEntity> listAll() {
        return em.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
    }

    @Transactional
    public void salvar(UserEntity entity) {
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    public void delete(UserEntity entity) {
        em.remove(em.contains(entity) ? entity : em.merge(entity));
    }
}
