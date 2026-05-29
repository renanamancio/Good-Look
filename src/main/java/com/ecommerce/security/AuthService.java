package com.ecommerce.security;

import com.ecommerce.model.bo.AuditLogBO;
import com.ecommerce.model.bo.ClienteBO;
import com.ecommerce.model.bo.UserBO;
import com.ecommerce.model.dao.RoleRepository;
import com.ecommerce.model.dao.UserRepository;
import com.ecommerce.model.dto.LoginRequestDTO;
import com.ecommerce.model.dto.LoginResponseDTO;
import com.ecommerce.model.dto.RegisterRequestDTO;
import com.ecommerce.model.entity.*;
import com.ecommerce.security.exception.BusinessException;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AuthService {

    @Inject
    UserRepository userRepository;

    @Inject
    RoleRepository roleRepository;

    @Inject
    UserBO userBO;

    @Inject
    ClienteBO clienteBO;

    @Inject
    AuditLogBO auditLogBO;

    @Inject
    JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request){
        UserEntity userEntity = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new BusinessException("E-mail ou senha inválidos"));

        if (!BcryptUtil.matches(request.senha(), userEntity.getSenha())) {
            throw new BusinessException("E-mail ou senha inválidos");
        }

        String token = jwtService.generateToken(userEntity.getId(), userEntity.getEmail(), userEntity.getAutorizacao().getNome());

        auditLogBO.registrar("LOGIN_SUCESSO", userEntity.getEmail(), "Login realizado com sucesso", null);

        return new LoginResponseDTO(
                token,
                userEntity.getEmail(),
                userEntity.getAutorizacao().getNome(),
                jwtService.getExpirationTime()
        );
    }

    @Transactional
    public LoginResponseDTO register(RegisterRequestDTO request){
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(request.email());
        userEntity.setSenha(request.senha());

        RoleEntity roleUser = roleRepository.findByNome(Role.USER)
                .orElseThrow(() -> new BusinessException("Role USER não encontrado"));
        userEntity.setAutorizacao(roleUser);

        userEntity = userBO.criar(userEntity);

        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNome(request.cliente().nome());
        clienteEntity.setCpf(request.cliente().cpf());
        clienteEntity.setDataNascimento(request.cliente().dataNascimento());
        clienteEntity.setTelefone(request.cliente().telefone());
        clienteEntity.setUserEntity(userEntity);

        clienteEntity = clienteBO.criar(clienteEntity);

        if (request.cliente().endereco() != null) {
            EnderecoEntity enderecoEntity = new EnderecoEntity();
            enderecoEntity.setLogradouro(request.cliente().endereco().logradouro());
            enderecoEntity.setNumero(request.cliente().endereco().numero());
            enderecoEntity.setComplemento(request.cliente().endereco().complemento());
            enderecoEntity.setBairro(request.cliente().endereco().bairro());
            enderecoEntity.setCidade(request.cliente().endereco().cidade());
            enderecoEntity.setUf(request.cliente().endereco().uf());
            enderecoEntity.setCep(request.cliente().endereco().cep());
            enderecoEntity.setQuadra(request.cliente().endereco().quadra());
            enderecoEntity.setLote(request.cliente().endereco().lote());
            enderecoEntity.setClienteEntity(clienteEntity);

            clienteEntity.setEnderecoEntity(enderecoEntity);
        }

        String token = jwtService.generateToken(userEntity.getId(), userEntity.getEmail(), userEntity.getAutorizacao().getNome());

        auditLogBO.registrar("REGISTRO_SUCESSO", userEntity.getEmail(),
                "Novo usuário registrado: " + clienteEntity.getNome(), null);

        return new LoginResponseDTO(
                token,
                userEntity.getEmail(),
                userEntity.getAutorizacao().getNome(),
                jwtService.getExpirationTime()
        );
    }
}
