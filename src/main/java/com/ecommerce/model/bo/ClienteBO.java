package com.ecommerce.model.bo;

import com.ecommerce.model.dao.ClienteRepository;
import com.ecommerce.model.entity.ClienteEntity;
import com.ecommerce.security.exception.BusinessException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class ClienteBO {
    @Inject
    ClienteRepository clienteRepository;

    @Inject
    AuditLogBO auditLogBO;

    private static final int IDADE_MINIMA = 18;

    public void validarIdadeMinima(LocalDate dataNascimento){
        LocalDate hoje = LocalDate.now();
        Period periodo = Period.between(dataNascimento, hoje);
        int idade = periodo.getYears();

        if (idade < IDADE_MINIMA){
            throw new BusinessException(
                    String.format("Cadastro não permitido. É necessário ter no mínimo %d anos. Idade atual: %d anos",  IDADE_MINIMA, idade)
            );
        }
    }

    @Transactional
    public ClienteEntity criar(ClienteEntity clienteEntity){
        validarIdadeMinima(clienteEntity.getDataNascimento());

        if(clienteRepository.existsByCpf(clienteEntity.getCpf())){
            throw new BusinessException("Já existe um clienteEntity cadastrado com esse CPF");
        }
        clienteRepository.persist(clienteEntity);

        auditLogBO.registrar("CRIAR_CLIENTE", clienteEntity.getUserEntity() != null ? clienteEntity.getUserEntity().getEmail() : null, "ClienteEntity " + clienteEntity.getNome() + " criado", null);

        return clienteEntity;
    }

    @Transactional
    public ClienteEntity atualizar(UUID id, ClienteEntity dadosAtualizados){
        ClienteEntity clienteEntityExistente = clienteRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("ClienteEntity não encontrado"));

        if (dadosAtualizados.getDataNascimento() != null && !dadosAtualizados.getDataNascimento().equals(clienteEntityExistente.getDataNascimento())) {
            validarIdadeMinima(dadosAtualizados.getDataNascimento());
            clienteEntityExistente.setDataNascimento(dadosAtualizados.getDataNascimento());
        }

        if (dadosAtualizados.getCpf() != null
                && !dadosAtualizados.getCpf().equals(clienteEntityExistente.getCpf())) {
            if (clienteRepository.existsByCpf(dadosAtualizados.getCpf())) {
                throw new BusinessException("CPF já está em uso");
            }
            clienteEntityExistente.setCpf(dadosAtualizados.getCpf());
        }

        if (dadosAtualizados.getNome() != null) {
            clienteEntityExistente.setNome(dadosAtualizados.getNome());
        }

        if (dadosAtualizados.getTelefone() != null) {
            clienteEntityExistente.setTelefone(dadosAtualizados.getTelefone());
        }

        auditLogBO.registrar("ATUALIZAR_CLIENTE",
                clienteEntityExistente.getUserEntity() != null ? clienteEntityExistente.getUserEntity().getEmail() : null,
                "ClienteEntity " + clienteEntityExistente.getNome() + " atualizado", null);

        return clienteEntityExistente;
    }

    @Transactional
    public void excluir(UUID id, String emailExecutor) {
        ClienteEntity clienteEntity = clienteRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("ClienteEntity não encontrado"));

        auditLogBO.registrar("EXCLUIR_CLIENTE", emailExecutor,
                "ClienteEntity " + clienteEntity.getNome() + " excluído", null);

        clienteRepository.delete(clienteEntity);
    }

    public ClienteEntity buscarPorId(UUID id) {
        return clienteRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("ClienteEntity não encontrado"));
    }

    public ClienteEntity buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new BusinessException("ClienteEntity não encontrado"));
    }

    public List<ClienteEntity> listarTodos() {
        return clienteRepository.listAll();
    }
}
