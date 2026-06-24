package com.ecommerce.model.bo;

import com.ecommerce.model.dao.UserDAO;
import com.ecommerce.model.dto.UserResponseDTO;
import com.ecommerce.model.dto.UserUpdateDTO;
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
import java.util.stream.Collectors;

/**
 * Objeto de Negócio (BO) para Usuários.
 * Contém regras de hierarquia e gestão de contas.
 */
@ApplicationScoped
public class UserBO {

    @Inject
    UserDAO userDAO;

    @Inject
    AuditLogBO auditLogBO;

    /**
     * Valida se o executor tem permissão para excluir o alvo.
     */
    public void validarHierarquiaExclusao(Role roleExecutor, Role roleAlvo){
        if (roleExecutor == Role.USER){
            throw new UnauthorizedException("Usuários comuns não têm permissão para excluir outros usuários");
        }
    }

    /**
     * Valida se o executor tem permissão para editar o alvo.
     */
    public void validarHierarquiaEdicao(Role roleExecutor, Role roleAlvo){
        if (roleExecutor == Role.USER){
            throw new UnauthorizedException("Usuários comuns não têm permissão para editar outros usuários");
        }
    }

    @Transactional
    public UserEntity criar(UserEntity userEntity){
        if(userDAO.existsByEmail(userEntity.getEmail())){
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail");
        }

        userEntity.setSenha(BcryptUtil.bcryptHash(userEntity.getSenha()));
        userDAO.salvar(userEntity);
        auditLogBO.registrar("CRIAR_USUARIO", userEntity.getEmail(), "Novo usuário criado", null);

        return userEntity;
    }

    @Transactional
    public UserResponseDTO atualizar(UUID id, UserUpdateDTO dto, String emailExecutor, Role roleExecutor){
        UserEntity entity = userDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Permite que o próprio usuário edite seus dados. Valida permissões/hierarquia apenas para terceiros.
        if (!entity.getEmail().equals(emailExecutor)) {
            validarHierarquiaEdicao(roleExecutor, entity.getAutorizacao().getNome());
        }

        if (dto.email() != null){
            if(!dto.email().equals(entity.getEmail()) && userDAO.existsByEmail(dto.email())){
                throw new BusinessException("Email já está em uso");
            }
            entity.setEmail(dto.email());
        }

        if (dto.senha() != null && !dto.senha().isBlank()){
            entity.setSenha(BcryptUtil.bcryptHash(dto.senha()));
        }

        auditLogBO.registrar("ATUALIZAR_USUARIO", emailExecutor, "Usuário " + entity.getEmail() + " atualizado", null);

        return toDTO(entity);
    }

    @Transactional
    public void excluir(UUID id, String emailExecutor, Role roleExecutor){
        UserEntity entity = userDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado"));

        // Permite que o próprio usuário delete sua conta. Valida permissões/hierarquia apenas para terceiros.
        if (!entity.getEmail().equals(emailExecutor)) {
            validarHierarquiaExclusao(roleExecutor, entity.getAutorizacao().getNome());
        }

        auditLogBO.registrar("EXCLUIR_USUARIO", emailExecutor, "Usuário " + entity.getEmail() + " excluído", null);

        userDAO.delete(entity);
    }

    public UserResponseDTO buscarPorId(UUID id) {
        return toDTO(userDAO.findById(id)
                .orElseThrow(() -> new BusinessException("Usuário não encontrado")));
    }

    public List<UserResponseDTO> listarTodos() {
        return userDAO.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public UserResponseDTO toDTO(UserEntity entity) {
        return new UserResponseDTO(
                entity.getId(),
                entity.getEmail(),
                entity.getAutorizacao().getNome()
        );
    }
}
