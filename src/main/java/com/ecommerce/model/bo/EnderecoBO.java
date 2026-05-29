package com.ecommerce.model.bo;

import com.ecommerce.model.dao.EnderecoRepository;
import com.ecommerce.model.entity.EnderecoEntity;
import com.ecommerce.security.exception.BusinessException;
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
    public EnderecoEntity criar(EnderecoEntity enderecoEntity) {
        enderecoRepository.persist(enderecoEntity);

        auditLogBO.registrar("CRIAR_ENDERECO",
                enderecoEntity.getClienteEntity() != null && enderecoEntity.getClienteEntity().getUserEntity() != null
                        ? enderecoEntity.getClienteEntity().getUserEntity().getEmail() : null,
                "Endereço criado para cliente " +
                        (enderecoEntity.getClienteEntity() != null ? enderecoEntity.getClienteEntity().getNome() : ""), null);

        return enderecoEntity;
    }

    @Transactional
    public EnderecoEntity atualizar(UUID id, EnderecoEntity dadosAtualizados) {
        EnderecoEntity enderecoEntityExistente = enderecoRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));

        if (dadosAtualizados.getLogradouro() != null) {
            enderecoEntityExistente.setLogradouro(dadosAtualizados.getLogradouro());
        }
        if (dadosAtualizados.getNumero() != null) {
            enderecoEntityExistente.setNumero(dadosAtualizados.getNumero());
        }
        if (dadosAtualizados.getComplemento() != null) {
            enderecoEntityExistente.setComplemento(dadosAtualizados.getComplemento());
        }
        if (dadosAtualizados.getBairro() != null) {
            enderecoEntityExistente.setBairro(dadosAtualizados.getBairro());
        }
        if (dadosAtualizados.getCidade() != null) {
            enderecoEntityExistente.setCidade(dadosAtualizados.getCidade());
        }
        if (dadosAtualizados.getUf() != null) {
            enderecoEntityExistente.setUf(dadosAtualizados.getUf());
        }
        if (dadosAtualizados.getCep() != null) {
            enderecoEntityExistente.setCep(dadosAtualizados.getCep());
        }
        if (dadosAtualizados.getQuadra() != null) {
            enderecoEntityExistente.setQuadra(dadosAtualizados.getQuadra());
        }
        if (dadosAtualizados.getLote() != null) {
            enderecoEntityExistente.setLote(dadosAtualizados.getLote());
        }

        auditLogBO.registrar("ATUALIZAR_ENDERECO",
                enderecoEntityExistente.getClienteEntity() != null && enderecoEntityExistente.getClienteEntity().getUserEntity() != null
                        ? enderecoEntityExistente.getClienteEntity().getUserEntity().getEmail() : null,
                "Endereço atualizado", null);

        return enderecoEntityExistente;
    }

    public EnderecoEntity buscarPorId(UUID id) {
        return enderecoRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Endereço não encontrado"));
    }
}
