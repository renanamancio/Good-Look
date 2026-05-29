package com.ecommerce.model.dao;

import com.ecommerce.model.entity.ClienteEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<ClienteEntity, UUID> {
    public Optional<ClienteEntity> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }
    public Optional<ClienteEntity> findByUserId(UUID userId) {
        return find("user.id", userId).firstResultOptional();
    }
}
