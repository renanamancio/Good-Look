package com.ecommerce.bo;

import com.ecommerce.dao.ClienteRepository;
import com.ecommerce.entity.Cliente;
import com.ecommerce.exception.BusinessException;
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
                    String.format("Cadastro não permitido. É necessário ter no mínimo %anos. Idade atual: %d anos",  IDADE_MINIMA, idade)
            );
        }
    }

    @Transactional
    public Cliente criar(Cliente cliente){
        validarIdadeMinima(cliente.getDataNascimento());

        if(clienteRepository.existsByCpf(cliente.getCpf())){
            throw new BusinessException("Já existe um cliente cadastrado com esse CPF");
        }
        clienteRepository.persist(cliente);

        auditLogBO.registrar("CRIAR_CLIENTE", cliente.getUser() != null ? cliente.getUser().getEmail() : null, "Cliente" + cliente.getNome() + "criado", null);

        return cliente;
    }

    @Transactional
    public Cliente atualizar(UUID id, Cliente dadosAtualizados){
        Cliente clienteExistente = clienteRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        if (dadosAtualizados.getDataNascimento() != null && !dadosAtualizados.getDataNascimento().equals(clienteExistente.getDataNascimento())) {
            validarIdadeMinima(dadosAtualizados.getDataNascimento());
            clienteExistente.setDataNascimento(dadosAtualizados.getDataNascimento());
        }

        if (dadosAtualizados.getCpf() != null
                && !dadosAtualizados.getCpf().equals(clienteExistente.getCpf())) {
            if (clienteRepository.existsByCpf(dadosAtualizados.getCpf())) {
                throw new BusinessException("CPF já está em uso");
            }
            clienteExistente.setCpf(dadosAtualizados.getCpf());
        }

        if (dadosAtualizados.getNome() != null) {
            clienteExistente.setNome(dadosAtualizados.getNome());
        }

        if (dadosAtualizados.getTelefone() != null) {
            clienteExistente.setTelefone(dadosAtualizados.getTelefone());
        }

        auditLogBO.registrar("ATUALIZAR_CLIENTE",
                clienteExistente.getUser() != null ? clienteExistente.getUser().getEmail() : null,
                "Cliente " + clienteExistente.getNome() + " atualizado", null);

        return clienteExistente;
    }

    @Transactional
    public void excluir(UUID id, String emailExecutor) {
        Cliente cliente = clienteRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        auditLogBO.registrar("EXCLUIR_CLIENTE", emailExecutor,
                "Cliente " + cliente.getNome() + " excluído", null);

        clienteRepository.delete(cliente);
    }

    public Cliente buscarPorId(UUID id) {
        return clienteRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }

    public Cliente buscarPorCpf(String cpf) {
        return clienteRepository.findByCpf(cpf)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }

    public List<Cliente> listarTodos() {
        return clienteRepository.listAll();
    }
}
