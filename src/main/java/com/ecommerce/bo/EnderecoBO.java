package com.ecommerce.bo;

import com.ecommerce.dao.EnderecoRepository;
import com.ecommerce.entity.Endereco;
import com.ecommerce.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.UUID;

@ApplicationScoped
public class EnderecoBO {

    @Inject
    EnderecoRepository enderecoRepository;

    @Inject
    AuditLogBO auditLogBO;

    @Transactional
    public Endereco criar(Endereco endereco) {
        enderecoRepository.persist(endereco);

        auditLogBO.registrar("CRIAR_ENDERECO",
                endereco.getCliente() != null && endereco.getCliente().getUser() != null
                        ? endereco.getCliente().getUser().getEmail() : null,
                "Endereço criado para cliente " +
                        (endereco.getCliente() != null ? endereco.getCliente().getNome() : ""), null);

        return endereco;
    }

    @Transactional
    public Endereco atualizar(UUID id, Endereco dadosAtualizados) {
        Endereco enderecoExistente = enderecoRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));

        if (dadosAtualizados.getLogradouro() != null) {
            enderecoExistente.setLogradouro(dadosAtualizados.getLogradouro());
        }
        if (dadosAtualizados.getNumero() != null) {
            enderecoExistente.setNumero(dadosAtualizados.getNumero());
        }
        if (dadosAtualizados.getComplemento() != null) {
            enderecoExistente.setComplemento(dadosAtualizados.getComplemento());
        }
        if (dadosAtualizados.getBairro() != null) {
            enderecoExistente.setBairro(dadosAtualizados.getBairro());
        }
        if (dadosAtualizados.getCidade() != null) {
            enderecoExistente.setCidade(dadosAtualizados.getCidade());
        }
        if (dadosAtualizados.getUf() != null) {
            enderecoExistente.setUf(dadosAtualizados.getUf());
        }
        if (dadosAtualizados.getCep() != null) {
            enderecoExistente.setCep(dadosAtualizados.getCep());
        }
        if (dadosAtualizados.getQuadra() != null) {
            enderecoExistente.setQuadra(dadosAtualizados.getQuadra());
        }
        if (dadosAtualizados.getLote() != null) {
            enderecoExistente.setLote(dadosAtualizados.getLote());
        }

        auditLogBO.registrar("ATUALIZAR_ENDERECO",
                enderecoExistente.getCliente() != null && enderecoExistente.getCliente().getUser() != null
                        ? enderecoExistente.getCliente().getUser().getEmail() : null,
                "Endereço atualizado", null);

        return enderecoExistente;
    }

    public Endereco buscarPorId(UUID id) {
        return enderecoRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));
    }
}
