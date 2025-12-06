package com.projeto.Models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "estoque")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EstoqueModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeItem; // Ex: "Algodão", "Henna Castanha"

    private String unidade; // Ex: "Pacote", "Caixa", "Unidade", "ml"

    // Quantidade atual no armário
    private BigDecimal quantidadeAtual;

    // Quantidade mínima para o sistema avisar que precisa comprar
    private BigDecimal quantidadeMinima;

    // Valor pago na última compra (para ajudar na precificação)
    private BigDecimal custoUltimaCompra;

    private LocalDate dataUltimaAtualizacao;
}