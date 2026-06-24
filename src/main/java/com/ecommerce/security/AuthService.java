package com.ecommerce.security;

import com.ecommerce.model.bo.AuditLogBO;
import com.ecommerce.model.bo.UserBO;
import com.ecommerce.model.dao.RoleDAO;
import com.ecommerce.model.dao.UserDAO;
import com.ecommerce.model.dto.LoginRequestDTO;
import com.ecommerce.model.dto.LoginResponseDTO;
import com.ecommerce.model.dto.RegisterRequestDTO;
import com.ecommerce.model.entity.*;
import com.ecommerce.security.exception.BusinessException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

/**
 * Serviço de Autenticação que orquestra o login e o registro de usuários.
 */
@ApplicationScoped
public class AuthService {

    @Inject
    UserDAO userDAO;

    @Inject
    RoleDAO roleDAO;

    @Inject
    UserBO userBO;

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request){
        UserEntity userEntity = userDAO.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos"));

        if (!BcryptUtil.matches(request.senha(), userEntity.getSenha())) {
            throw new BusinessException("E-mail ou senha inválidos");
        }

        Role role = userEntity.getAutorizacao().getNome();
        String token = jwtService.generateToken(userEntity.getId(), userEntity.getEmail(), role);

        auditLogBO.registrar("LOGIN_SUCESSO", userEntity.getEmail(), "Login realizado com sucesso", null);

        return new LoginResponseDTO(
                token,
                userBO.toDTO(userEntity),
                jwtService.getExpirationTime()
        );
    }

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request){
        UserEntity userEntity = new UserEntity();
        userEntity.setNome(request.nome());
        userEntity.setEmail(request.email());
        userEntity.setSenha(request.senha());

        // Define a role baseado no campo administrador
        Role roleEnum = Boolean.TRUE.equals(request.administrador()) ? Role.ADMIN : Role.USER;

        RoleEntity roleEntity = roleDAO.findByNome(roleEnum)
                .orElseThrow(() -> new BusinessException("Role " + roleEnum.name() + " não encontrado"));
        userEntity.setAutorizacao(roleEntity);

        userEntity = userBO.criar(userEntity);

        Role role = userEntity.getAutorizacao().getNome();
        String token = jwtService.generateToken(userEntity.getId(), userEntity.getEmail(), role);

        auditLogBO.registrar("REGISTRO_SUCESSO", userEntity.getEmail(),
                "Novo usuário registrado: " + userEntity.getNome(), null);

        return new LoginResponseDTO(
                token,
                userBO.toDTO(userEntity),
                jwtService.getExpirationTime()
        );
    }
}
