package com.ecommerce.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.github.f4b6a3.uuid.UuidCreator;
import java.util.UUID;

@Entity
@Table(name = "tbperfil", uniqueConstraints ={
        @UniqueConstraint(columnNames = "nome", name = "tbperfil_nome_uk")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleEntity {

        @Id
        @Column(columnDefinition = "UUID")
        private UUID id;

        @Column(nullable = false)
        @Enumerated(EnumType.STRING)
        private Role nome;

        @PrePersist
        protected void onCreate() {
                if (this.id == null) {
                        this.id = UuidCreator.getTimeOrderedEpoch();
                }
        }
}
