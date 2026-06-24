package com.ecommerce.model.bo;

import com.ecommerce.model.dao.AuditLogDAO;

import com.ecommerce.model.dto.AuditLogResponseDTO;
import com.ecommerce.model.entity.AuditLogEntity;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Objeto de Negócio (BO) para Auditoria.
 * Gerencia o registro e consulta de logs do sistema.
 */
@ApplicationScoped
public class AuditLogBO {

    @Inject
    AuditLogDAO auditLogDAO;

    /**
     * Registra uma nova ação no log de auditoria.
     *
     * @param acao Descrição da ação realizada.
     * @param usuarioEmail Email do usuário que realizou a ação.
     * @param detalhes Detalhes adicionais da ação.
     * @param ipAddress Endereço IP de origem.
     */
    @Transactional
    public void registrar(String acao, String usuarioEmail, String detalhes, String ipAddress) {
        AuditLogEntity log = new AuditLogEntity(acao, usuarioEmail, detalhes, ipAddress);
        auditLogDAO.persist(log);
    }

    /**
     * Lista todos os logs convertidos para DTO.
     *
     * @return Lista de AuditLogResponseDTO.
     */
    public List<AuditLogResponseDTO> listarTodos() {
        return auditLogDAO.listAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Converte entidade para DTO.
     */
    private AuditLogResponseDTO toDTO(AuditLogEntity entity) {
        return new AuditLogResponseDTO(
                entity.getId(),
                entity.getAcao(),
                entity.getUsuarioEmail(),
                entity.getDataHora(),
                entity.getDetalhes(),
                entity.getIpAddress()
        );
    }
}
