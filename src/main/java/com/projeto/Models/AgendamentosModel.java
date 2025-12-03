package com.projeto.Models;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList; // Importante
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "agendamentos")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgendamentosModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate data;

    private String horaInicial;
    private String horaFinal;

    private String observacoes;
    private String status;

    private BigDecimal valorParcial; // Valor do Sinal
    private BigDecimal valorProcedimento; // Valor Total
    private BigDecimal valorDesconto; // Valor do Desconto Promocional
    private BigDecimal taxaSinalAplicada; // Taxa % usada no sinal

    private Boolean taxaSinalRepassada; // Se repassou o sinal

    // --- RELACIONAMENTOS ---

    @JsonIgnore
    @OneToMany(mappedBy = "agendamentos")
    private List<MapeamentosModel> mapeamentos;

    // Forma de pagamento exclusiva do SINAL (Opcional)
    @ManyToOne
    @JoinColumn(name = "forma_pagamento_sinal_id")
    private FormasPagamentoModel formaPagamentoSinal;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private ClientesModel clientes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "agendamento_procedimentos", joinColumns = @JoinColumn(name = "agendamento_id"), inverseJoinColumns = @JoinColumn(name = "procedimento_id"))
    private List<ProcedimentosModel> procedimentos = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "promocao_id")
    private PromocoesModel promocao;

    // Aqui entram os múltiplos pagamentos (Ex: R$ 50 no Pix + R$ 100 no Crédito)
    @OneToMany(mappedBy = "agendamento", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<PagamentosModel> pagamentos = new ArrayList<>();

    // Calcula o valor liquido virtualmente , para mandar para o dashboard ( React )
    public BigDecimal getValorLiquidoTotal() {
        BigDecimal totalLiquido = BigDecimal.ZERO;

        // 1. CÁLCULO DO SINAL (Usando Snapshot)
        if (valorParcial != null && valorParcial.compareTo(BigDecimal.ZERO) > 0) {

            // Define a taxa e a regra a usar (Prioridade: Snapshot > Cadastro > Zero)
            BigDecimal taxaUsar = taxaSinalAplicada;
            Boolean repassarUsar = taxaSinalRepassada;

            // Fallback para dados antigos (se snapshot for nulo, tenta pegar do cadastro
            // atual)
            if (taxaUsar == null && formaPagamentoSinal != null)
                taxaUsar = formaPagamentoSinal.getTaxa();
            if (repassarUsar == null && formaPagamentoSinal != null)
                repassarUsar = formaPagamentoSinal.getRepassarTaxa();

            // Garante valores seguros
            if (taxaUsar == null)
                taxaUsar = BigDecimal.ZERO;
            if (repassarUsar == null)
                repassarUsar = false;

            // Executa a conta
            if (repassarUsar) {
                // Se repassou, o valorParcial já inclui a taxa, mas o líquido é o valorParcial
                // original (ou seja, entra cheio)
                // *Obs: Na lógica de repasse, o cliente paga X + Taxa. O que cai na conta é o
                // X.
                // Se o valorParcial for o que caiu na conta, somamos ele.
                totalLiquido = totalLiquido.add(valorParcial);
            } else {
                // Se o estabelecimento absorve, descontamos a taxa
                BigDecimal desconto = valorParcial.multiply(taxaUsar).divide(new BigDecimal("100"));
                totalLiquido = totalLiquido.add(valorParcial.subtract(desconto));
            }
        }

        // 2. CÁLCULO DA LISTA DE PAGAMENTOS (Usando Snapshot)
        if (pagamentos != null) {
            for (PagamentosModel pg : pagamentos) {
                if (pg.getValor() != null) {

                    BigDecimal taxaPg = pg.getTaxaAplicada();
                    Boolean repassaPg = pg.getTaxaRepassada();

                    // Fallback
                    if (taxaPg == null && pg.getFormaPagamento() != null)
                        taxaPg = pg.getFormaPagamento().getTaxa();
                    if (repassaPg == null && pg.getFormaPagamento() != null)
                        repassaPg = pg.getFormaPagamento().getRepassarTaxa();

                    if (taxaPg == null)
                        taxaPg = BigDecimal.ZERO;
                    if (repassaPg == null)
                        repassaPg = false;

                    if (repassaPg) {
                        totalLiquido = totalLiquido.add(pg.getValor());
                    } else {
                        BigDecimal desconto = pg.getValor().multiply(taxaPg).divide(new BigDecimal("100"));
                        totalLiquido = totalLiquido.add(pg.getValor().subtract(desconto));
                    }
                }
            }
        }

        return totalLiquido;
    }
}