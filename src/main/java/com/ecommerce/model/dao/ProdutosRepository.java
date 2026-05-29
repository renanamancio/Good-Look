package com.ecommerce.model.dao;

import com.ecommerce.model.entity.ProdutosEntity;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.Optional;
import java.util.UUID;

@ApplicationScoped
public class ProdutosRepository implements PanacheRepository<ProdutosEntity> {
    public Optional<ProdutosEntity> findByUrl(String url) {
        return find("url", url).firstResultOptional();
    }
}
