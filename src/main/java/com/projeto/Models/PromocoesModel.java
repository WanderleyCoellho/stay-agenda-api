package com.projeto.Models;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "promocoes")
@Data
@AllArgsConstructor
@NoArgsConstructor

public class PromocoesModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String descricao; // Ex: "Black Friday", "Desconto Amigo"

    // --- REGRAS DE VALIDADE ---
    private LocalDate dataInicio;
    private LocalDate dataFim; // Se for NULL, é indeterminado

    @Enumerated(EnumType.STRING)
    private StatusPromocao status; // ATIVA, PAUSADA, CANCELADA

    // --- REGRAS FINANCEIRAS ---
    private BigDecimal valorPromocional; // Ex: 10.00

    @Enumerated(EnumType.STRING)
    private TipoDesconto tipoDesconto; // FIXO (R$) ou PORCENTAGEM (%)

    // --- ALCANCE ---
    // Se estiver NULL, aplica a TODOS os procedimentos
    @ManyToOne
    @JoinColumn(name = "procedimento_id")
    private ProcedimentosModel procedimento;
    
    // --- ENUMS AUXILIARES (Pode criar em arquivos separados se preferir) ---
    public enum StatusPromocao {
        ATIVA,
        PAUSADA,
        CANCELADA
    }

    public enum TipoDesconto {
        FIXO,       // Ex: Desconto de R$ 10,00
        PORCENTAGEM // Ex: Desconto de 10%
    }
}
