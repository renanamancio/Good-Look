package com.ecommerce.model.bo;

import com.ecommerce.model.dao.ProdutoDAO;
import com.ecommerce.model.dto.ProdutoCreateDTO;
import com.ecommerce.model.dto.ProdutoResponseDTO;
import com.ecommerce.model.dto.ProdutoUpdateDTO;
import com.ecommerce.model.entity.ProdutoEntity;
import com.ecommerce.security.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.UUID;

/**
 * Objeto de Negócio (BO) para a gestão de produtos.
 * Encapsula regras de negócio e orquestra a comunicação entre Controller e DAO.
 */
@ApplicationScoped
public class ProdutosBO {

    @Inject
    ProdutoDAO produtoDAO;

    public List<ProdutoResponseDTO> listarTodos() {
        return produtoDAO.listarTodos();
    }

    public ProdutoResponseDTO buscarPorId(UUID id) {
        ProdutoEntity entity = produtoDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));
        return toDTO(entity);
    }

    public ProdutoResponseDTO criar(ProdutoCreateDTO dto) {
        if (produtoDAO.findByUrl(dto.url()).isPresent()) {
            throw new BusinessException("Já existe um produto com esta URL");
        }

        ProdutoEntity entity = new ProdutoEntity();
        mapToEntity(dto, entity);
        produtoDAO.salvar(entity);
        return toDTO(entity);
    }

    public ProdutoResponseDTO atualizar(UUID id, ProdutoUpdateDTO dto) {
        ProdutoEntity entity = produtoDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        mapToEntity(dto, entity);
        produtoDAO.salvar(entity);
        return toDTO(entity);
    }

    public void deletar(UUID id) {
        produtoDAO.deletar(id);
    }

    private void mapToEntity(ProdutoCreateDTO dto, ProdutoEntity entity) {
        entity.setNome(dto.nome());
        entity.setDescricao(dto.descricao());
        entity.setPreco(dto.preco());
        entity.setQuantidade(dto.quantidade());
        entity.setImagem(dto.imagem());
        entity.setUrl(dto.url());
    }

    private void mapToEntity(ProdutoUpdateDTO dto, ProdutoEntity entity) {
        if (dto.nome() != null) entity.setNome(dto.nome());
        if (dto.descricao() != null) entity.setDescricao(dto.descricao());
        if (dto.preco() != null) entity.setPreco(dto.preco());
        if (dto.quantidade() != null) entity.setQuantidade(dto.quantidade());
        if (dto.imagem() != null) entity.setImagem(dto.imagem());
        if (dto.url() != null) entity.setUrl(dto.url());
    }

    public ProdutoResponseDTO toDTO(ProdutoEntity entity) {
        return new ProdutoResponseDTO(
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
