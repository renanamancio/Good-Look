package com.ecommerce.model.dao;

import com.ecommerce.model.entity.EnderecoEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepositoryBase<EnderecoEntity, UUID> {
    public Optional<EnderecoEntity> findByClienteId(UUID clienteId) {
        return find("cliente.id", clienteId).firstResultOptional();
    }
}
