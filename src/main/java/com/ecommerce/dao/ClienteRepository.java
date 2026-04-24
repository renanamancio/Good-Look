package com.ecommerce.dao;

import com.ecommerce.entity.Cliente;
import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ClienteRepository implements PanacheRepositoryBase<Cliente, UUID> {
    public Optional<Cliente> findByCpf(String cpf) {
        return find("cpf", cpf).firstResultOptional();
    }
    public boolean existsByCpf(String cpf) {
        return count("cpf", cpf) > 0;
    }
    public Optional<Cliente> findByUserId(UUID userId) {
        return find("user.id", userId).firstResultOptional();
    }
}
