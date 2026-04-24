package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "enderecos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String logradouro;

    private String quadra;
    private String lote;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String uf;

    @Column(nullable = false, length = 9)
    private String cep;

    @OneToOne
    @JoinColumn(name = "cliente_id", nullable = false, unique = true)
    private Cliente cliente;
}
