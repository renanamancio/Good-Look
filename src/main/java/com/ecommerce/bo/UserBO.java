package com.ecommerce.bo;

import com.ecommerce.dao.UserRepository;
import com.ecommerce.entity.Role;
import com.ecommerce.entity.User;
import com.ecommerce.exception.BusinessException;
import com.ecommerce.exception.UnauthorizedException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class UserBO {

    @Inject
    UserRepository userRepository;

    @Inject
    AuditLogBO auditLogBO;

    public void validarHierarquiaExclusao(Role roleExecutor, Role roleAlvo){
        if (roleExecutor == Role.USER){
            throw new UnauthorizedException("Usuários comuns não têm permissão para excluir outros usuários");
        }
        if(roleExecutor == Role.ADMIN && roleAlvo == Role.USER){
            throw new UnauthorizedException("Administradores padrão só podem excluir usuários comuns");
        }
    }


    public void validarHierarquiaEdicao(Role roleExecutor, Role roleAlvo){
        if (roleExecutor == Role.USER){
            throw new UnauthorizedException("Usuários comuns não têm permissão para editar outros usuários");
        }
        if(roleExecutor == Role.ADMIN && roleAlvo == Role.USER){
            throw new UnauthorizedException("Administradores padrão só podem editar usuários comuns");
        }
    }

    @Transactional
    public User criar(User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }

        user.setSenha(BcryptUtil.bcryptHash(user.getSenha()));

        userRepository.persist(user);
        auditLogBO.registrar("CRIAR_USUARIO", user.getEmail(), "Novo usuário criado", null);

        return user;
    }

    @Transactional
    public User atualizar(UUID id, User dadosAtualizados, String emailExecutor, Role roleExecutor){
        User userExistente = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        validarHierarquiaEdicao(roleExecutor, userExistente.getAutorizacao());

        if (dadosAtualizados.getEmail() != null){
            if(!dadosAtualizados.getEmail().equals(userExistente.getEmail()) && userRepository.existsByEmail(dadosAtualizados.getEmail())){
                throw new BusinessException("Email já está em uso");
            }
            userExistente.setEmail(dadosAtualizados.getEmail());
        }

        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isBlank()){
            userExistente.setSenha(BcryptUtil.bcryptHash(dadosAtualizados.getSenha()));
        }

        auditLogBO.registrar("ATUALIZAR_USUARIO", emailExecutor, "Usuário " + userExistente.getEmail() + " atualizado", null);

        return userExistente;
    }

    @Transactional
    public void excluir(UUID id, String emailExecutor, Role roleExecutor){
        User user = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        validarHierarquiaExclusao(roleExecutor, user.getAutorizacao());

        auditLogBO.registrar("EXCLUIR_USUARIO", emailExecutor, "Usuário " + user.getEmail() + " excluído", null);

        userRepository.delete(user);
    }

    public User buscarPorId(UUID id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    public User buscarPorEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    public List<User> listarTodos() {
        return userRepository.listAll();
    }
}
