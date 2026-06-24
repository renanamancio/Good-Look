package com.ecommerce.model.bo;

import com.ecommerce.model.dao.CarrinhoDAO;
import com.ecommerce.model.dao.ProdutoDAO;
import com.ecommerce.model.dao.UserDAO;
import com.ecommerce.model.dto.CarrinhoResponseDTO;
import com.ecommerce.model.dto.ItemCarrinhoResponseDTO;
import com.ecommerce.model.entity.*;
import com.ecommerce.security.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class CarrinhoBO {

    @Inject
    CarrinhoDAO carrinhoDAO;

    @Inject
    ProdutoDAO produtoDAO;

    @Inject
    UserDAO userDAO;

    @Transactional
    public CarrinhoEntity obterOuCriarCarrinho(UUID userId) {
        return carrinhoDAO.findByUserId(userId)
                .orElseGet(() -> {
                    UserEntity user = userDAO.findById(userId)
                            .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
                    CarrinhoEntity novo = new CarrinhoEntity();
                    novo.setUser(user);
                    novo.setDataCriacao(LocalDateTime.now());
                    novo.setPrecoTotal(0.0);
                    novo.setQuantidadeTotal(0.0);
                    novo.setItens(new ArrayList<>());
                    carrinhoDAO.persist(novo);
                    return novo;
                });
    }

    @Transactional
    public void adicionarItem(UUID userId, UUID produtoId, Integer quantidade) {
        CarrinhoEntity carrinho = obterOuCriarCarrinho(userId);
        ProdutoEntity produto = produtoDAO.findById(produtoId)
                .orElseThrow(() -> new BusinessException("Produto não encontrado"));

        if (produto.getQuantidade() < quantidade) {
            throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
        }

        ItemCarrinhoEntity itemExistente = carrinho.getItens().stream()
                .filter(i -> i.getProduto().getId().equals(produtoId))
                .findFirst()
                .orElse(null);

        if (itemExistente != null) {
            itemExistente.setQuantidade(itemExistente.getQuantidade() + quantidade);
        } else {
            ItemCarrinhoEntity novoItem = new ItemCarrinhoEntity();
            novoItem.setCarrinho(carrinho);
            novoItem.setProduto(produto);
            novoItem.setQuantidade(quantidade);
            novoItem.setPrecoUnitario(produto.getPreco());
            carrinho.getItens().add(novoItem);
        }

        recalcularTotais(carrinho);
    }

    @Transactional
    public void removerItem(UUID userId, UUID produtoId) {
        CarrinhoEntity carrinho = obterOuCriarCarrinho(userId);
        carrinho.getItens().removeIf(i -> i.getProduto().getId().equals(produtoId));
        recalcularTotais(carrinho);
    }

    @Transactional
    public void limparCarrinho(UUID userId) {
        CarrinhoEntity carrinho = obterOuCriarCarrinho(userId);
        carrinho.getItens().clear();
        recalcularTotais(carrinho);
    }

    private void recalcularTotais(CarrinhoEntity carrinho) {
        double totalPreco = 0.0;
        double totalQtd = 0.0;
        for (ItemCarrinhoEntity item : carrinho.getItens()) {
            totalPreco += item.getPrecoUnitario() * item.getQuantidade();
            totalQtd += item.getQuantidade();
        }
        carrinho.setPrecoTotal(totalPreco);
        carrinho.setQuantidadeTotal(totalQtd);
    }

    public CarrinhoResponseDTO toDTO(CarrinhoEntity entity) {
        return new CarrinhoResponseDTO(
                entity.getId(),
                entity.getPrecoTotal(),
                entity.getQuantidadeTotal(),
                entity.getItens().stream().map(this::itemToDTO).collect(Collectors.toList())
        );
    }

    private ItemCarrinhoResponseDTO itemToDTO(ItemCarrinhoEntity entity) {
        return new ItemCarrinhoResponseDTO(
                entity.getId(),
                entity.getProduto().getId(),
                entity.getProduto().getNome(),
                entity.getPrecoUnitario(),
                entity.getQuantidade()
        );
    }
}
