package com.projeto.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;

@Entity
@Table(name = "pagamentos_realizados") // Nome da tabela no banco
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PagamentosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal valor; // Quanto o cliente pagou nessa forma
    private BigDecimal taxaAplicada; // taxa da maquina cobrada no agendamento Ex: 3.5

    private Boolean taxaRepassada;   // se taxa foi repassada pro cliente Ex: true/false

    // Relacionamento com a forma (para saber se é Pix, Cartão, e qual a taxa)
    @ManyToOne
    @JoinColumn(name = "forma_pagamento_id")
    private FormasPagamentoModel formaPagamento;

    // Relacionamento com o Agendamento
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "agendamento_id")
    private AgendamentosModel agendamento;
}