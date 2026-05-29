package com.ecommerce.model.bo;

import com.ecommerce.model.dao.ProdutosRepository;
import com.ecommerce.model.dto.ProdutosDTO;
import com.ecommerce.model.entity.ProdutosEntity;
import com.ecommerce.security.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class ProdutosBO {

    @Inject
    ProdutosRepository produtosRepository;

    public List<ProdutosDTO> listarTodos() {
        return produtosRepository.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProdutosDTO buscarPorId(UUID id) {
        ProdutosEntity entity = produtosRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));
        return toDTO(entity);
    }

    @Transactional
    public ProdutosDTO criar(ProdutosDTO dto) {
        if (produtosRepository.findByUrl(dto.url()).isPresent()) {
            throw new BusinessException("Já existe um produto com esta URL");
        }

        ProdutosEntity entity = new ProdutosEntity();
        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setPreco(dto.preco());
        entity.setQuantidade(dto.quantidade());
        entity.setImagem(dto.imagem());
        entity.setUrl(dto.url());

        produtosRepository.persist(entity);
        return toDTO(entity);
    }

    @Transactional
    public ProdutosDTO atualizar(UUID id, ProdutosDTO dto) {
        ProdutosEntity entity = produtosRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setPreco(dto.preco());
        entity.setQuantidade(dto.quantidade());
        entity.setImagem(dto.imagem());
        entity.setUrl(dto.url());

        return toDTO(entity);
    }

    @Transactional
    public void deletar(UUID id) {
        produtosRepository.deleteById(id);
    }

    private ProdutosDTO toDTO(ProdutosEntity entity) {
        return new ProdutosDTO(
                entity.getId(),
                entity.getNome(),
                entity.getDescricao(),
                entity.getPreco(),
                entity.getQuantidade(),
                entity.getImagem(),
                entity.getUrl()
        );
    }
}
