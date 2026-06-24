package com.ecommerce.model.bo;

import com.ecommerce.model.dao.PedidoDAO;
import com.ecommerce.model.dto.ItemPedidoResponseDTO;
import com.ecommerce.model.dto.PedidoResponseDTO;
import com.ecommerce.model.entity.*;
import com.ecommerce.security.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@ApplicationScoped
public class PedidoBO {

    @Inject
    PedidoDAO pedidoDAO;

    @Inject
    CarrinhoBO carrinhoBO;

    @Inject
    AuditLogBO auditLogBO;

    @Transactional
    public PedidoResponseDTO finalizarPedido(UUID userId) {
        CarrinhoEntity carrinho = carrinhoBO.obterOuCriarCarrinho(userId);

        if (carrinho.getItens().isEmpty()) {
            throw new BusinessException("O carrinho está vazio");
        }

        PedidoEntity pedido = new PedidoEntity();
        pedido.setUser(carrinho.getUser());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setValorTotal(carrinho.getPrecoTotal());
        pedido.setStatus("FINALIZADO");
        pedido.setItens(new ArrayList<>());

        for (ItemCarrinhoEntity itemCarrinho : carrinho.getItens()) {
            ProdutoEntity produto = itemCarrinho.getProduto();
            
            // Validação de estoque final
            if (produto.getQuantidade() < itemCarrinho.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            // Baixa de estoque
            produto.setQuantidade(produto.getQuantidade() - itemCarrinho.getQuantidade());

            ItemPedidoEntity itemPedido = new ItemPedidoEntity();
            itemPedido.setPedido(pedido);
            itemPedido.setProduto(produto);
            itemPedido.setQuantidade(itemCarrinho.getQuantidade());
            itemPedido.setPrecoUnitario(itemCarrinho.getPrecoUnitario());
            itemPedido.setPrecoTotal(itemCarrinho.getPrecoUnitario() * itemCarrinho.getQuantidade());
            
            pedido.getItens().add(itemPedido);
        }

        pedidoDAO.persist(pedido);
        carrinhoBO.limparCarrinho(userId);

        auditLogBO.registrar("FINALIZAR_PEDIDO", pedido.getUser().getEmail(), 
                "Pedido finalizado: " + pedido.getId(), null);

        return toDTO(pedido);
    }

    public List<PedidoResponseDTO> listarPorUsuario(UUID userId) {
        return pedidoDAO.findByUserId(userId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public PedidoResponseDTO buscarPorId(UUID id) {
        PedidoEntity entity = pedidoDAO.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Pedido não encontrado"));
        return toDTO(entity);
    }

    public PedidoResponseDTO toDTO(PedidoEntity entity) {
        return new PedidoResponseDTO(
                entity.getId(),
                entity.getDataPedido(),
                entity.getValorTotal(),
                entity.getStatus(),
                entity.getItens().stream().map(this::itemToDTO).collect(Collectors.toList())
        );
    }

    private ItemPedidoResponseDTO itemToDTO(ItemPedidoEntity entity) {
        return new ItemPedidoResponseDTO(
                entity.getId(),
                entity.getProduto().getId(),
                entity.getProduto().getNome(),
                entity.getQuantidade(),
                entity.getPrecoUnitario(),
                entity.getPrecoTotal()
        );
    }
}
