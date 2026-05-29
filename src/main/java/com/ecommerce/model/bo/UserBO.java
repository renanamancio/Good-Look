package com.ecommerce.model.bo;

import com.ecommerce.model.dao.UserRepository;
import com.ecommerce.model.entity.Role;
import com.ecommerce.model.entity.UserEntity;
import com.ecommerce.security.exception.BusinessException;
import com.ecommerce.security.exception.UnauthorizedException;
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
    public UserEntity criar(UserEntity userEntity){
        if(userRepository.existsByEmail(userEntity.getEmail())){
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }

        userEntity.setSenha(BcryptUtil.bcryptHash(userEntity.getSenha()));

        userRepository.persist(userEntity);
        auditLogBO.registrar("CRIAR_USUARIO", userEntity.getEmail(), "Novo usuário criado", null);

        return userEntity;
    }

    @Transactional
    public UserEntity atualizar(UUID id, UserEntity dadosAtualizados, String emailExecutor, Role roleExecutor){
        UserEntity userEntityExistente = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        validarHierarquiaEdicao(roleExecutor, userEntityExistente.getAutorizacao().getNome());

        if (dadosAtualizados.getEmail() != null){
            if(!dadosAtualizados.getEmail().equals(userEntityExistente.getEmail()) && userRepository.existsByEmail(dadosAtualizados.getEmail())){
                throw new BusinessException("Email já está em uso");
            }
            userEntityExistente.setEmail(dadosAtualizados.getEmail());
        }

        if (dadosAtualizados.getSenha() != null && !dadosAtualizados.getSenha().isBlank()){
            userEntityExistente.setSenha(BcryptUtil.bcryptHash(dadosAtualizados.getSenha()));
        }

        auditLogBO.registrar("ATUALIZAR_USUARIO", emailExecutor, "Usuário " + userEntityExistente.getEmail() + " atualizado", null);

        return userEntityExistente;
    }

    @Transactional
    public void excluir(UUID id, String emailExecutor, Role roleExecutor){
        UserEntity userEntity = userRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        validarHierarquiaExclusao(roleExecutor, userEntity.getAutorizacao().getNome());

        auditLogBO.registrar("EXCLUIR_USUARIO", emailExecutor, "Usuário " + userEntity.getEmail() + " excluído", null);

        userRepository.delete(userEntity);
    }

    public UserEntity buscarPorId(UUID id) {
        return userRepository.findByIdOptional(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    public UserEntity buscarPorEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));
    }

    public List<UserEntity> listarTodos() {
        return userRepository.listAll();
    }
}
