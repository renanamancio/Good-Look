package com.ecommerce.model.dao;

import com.ecommerce.model.dto.ProdutoResponseDTO;
import com.ecommerce.model.entity.ProdutoEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * DAO para a entidade ProdutoEntity utilizando EntityManager.
 */
@ApplicationScoped
public class ProdutoDAO {

    @Inject
    EntityManager em;

    /**
     * Utiliza Constructor Expression do JPQL para retornar DTOs diretamente.
     */
    public List<ProdutoResponseDTO> listarTodos() {
        String jpql = "SELECT new com.ecommerce.model.dto.ProdutoResponseDTO(p.id, p.nome, p.descricao, p.preco, p.quantidade, p.imagem, p.url) " +
                      "FROM ProdutoEntity p";
        return em.createQuery(jpql, ProdutoResponseDTO.class).getResultList();
    }

    public Optional<ProdutoEntity> findById(UUID id) {
        return Optional.ofNullable(em.find(ProdutoEntity.class, id));
    }

    public Optional<ProdutoEntity> findByUrl(String url) {
        return em.createQuery("SELECT p FROM ProdutoEntity p WHERE p.url = :url", ProdutoEntity.class)
                .setParameter("url", url)
                .getResultList()
                .stream()
                .findFirst();
    }

    @Transactional
    public void salvar(ProdutoEntity entity) {
        if (entity.getId() == null) {
            em.persist(entity);
        } else {
            em.merge(entity);
        }
    }

    @Transactional
    public void deletar(UUID id) {
        ProdutoEntity entity = em.find(ProdutoEntity.class, id);
        if (entity != null) {
            em.remove(entity);
        }
    }
}
