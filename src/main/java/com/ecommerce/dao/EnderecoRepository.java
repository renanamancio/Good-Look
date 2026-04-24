package com.ecommerce.dao;

import com.ecommerce.entity.Endereco;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class EnderecoRepository implements PanacheRepositoryBase<Endereco, UUID> {
    public Optional<Endereco> findByClienteId(UUID clienteId) {
        return find("cliente.id", clienteId).firstResultOptional();
    }
}
