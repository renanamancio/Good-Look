package com.ecommerce.model.dao;

import com.ecommerce.model.entity.Role;
import com.ecommerce.model.entity.RoleEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class RoleRepository implements PanacheRepositoryBase<RoleEntity, UUID> {
    public Optional<RoleEntity> findByNome(Role nome) {
        return find("nome", nome).firstResultOptional();
    }
}
