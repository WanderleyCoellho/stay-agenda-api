package com.projeto.Services;

import com.projeto.dto.MovimentacaoFinanceiraDTO;
import com.projeto.Models.AgendamentosModel;
import com.projeto.Models.PagamentosModel;
import com.projeto.Models.DespesasModel;
import com.projeto.Repositories.AgendamentosRepository; // Precisa injetar este
import com.projeto.Repositories.PagamentosRepository;
import com.projeto.Repositories.DespesasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class FinanceiroService {

    @Autowired
    private AgendamentosRepository agendamentosRepository; // INJETAR REPOSITORIO DE AGENDAMENTOS

    @Autowired
    private PagamentosRepository pagamentosRepository;

    @Autowired
    private DespesasRepository despesasRepository;

    public List<MovimentacaoFinanceiraDTO> gerarExtratoUnificado() {
        List<MovimentacaoFinanceiraDTO> extrato = new ArrayList<>();

        // =========================================================================
        // 1. PROCESSAR SINAIS (Entradas Antecipadas vindas de Agendamentos)
        // =========================================================================
        List<AgendamentosModel> agendamentos = agendamentosRepository.findAll();

        for (AgendamentosModel ag : agendamentos) {
            // Verifica se existe valor de sinal (> 0)
            if (ag.getValorParcial() != null && ag.getValorParcial().compareTo(BigDecimal.ZERO) > 0) {

                // Determinar Taxa e Repasse (Prioridade: Snapshot no Agendamento > Cadastro da
                // Forma)
                BigDecimal taxaSinal = ag.getTaxaSinalAplicada();
                Boolean repassaSinal = ag.getTaxaSinalRepassada();

                // Fallback: Se não tem salvo no agendamento, pega do cadastro da forma de
                // pagamento
                if (taxaSinal == null && ag.getFormaPagamentoSinal() != null) {
                    taxaSinal = ag.getFormaPagamentoSinal().getTaxa();
                }
                if (repassaSinal == null && ag.getFormaPagamentoSinal() != null) {
                    repassaSinal = ag.getFormaPagamentoSinal().getRepassarTaxa();
                }

                // Calcular Líquido do Sinal
                BigDecimal liquidoSinal = calcularValorLiquido(ag.getValorParcial(), taxaSinal, repassaSinal);

                // Nome do Cliente
                String nomeCliente = (ag.getClientes() != null) ? ag.getClientes().getNome() : "Cliente Avulso";
                String nomeForma = (ag.getFormaPagamentoSinal() != null) ? ag.getFormaPagamentoSinal().getNome()
                        : "Sinal";

                extrato.add(new MovimentacaoFinanceiraDTO(
                        ag.getId() * 10000, // ID "fictício" para diferenciar do pagamento normal na listagem
                        ag.getData(), // Data do agendamento (Ideal seria ter data_pagamento_sinal)
                        "Entrada (Sinal) - " + nomeCliente,
                        liquidoSinal,
                        "ENTRADA",
                        nomeForma));
            }
        }

        // =========================================================================
        // 2. PROCESSAR PAGAMENTOS RESTANTES (Lista de Pagamentos)
        // =========================================================================
        List<PagamentosModel> pagamentos = pagamentosRepository.findAll();

        for (PagamentosModel pg : pagamentos) {

            // Determinar Taxa e Repasse
            BigDecimal taxaPg = pg.getTaxaAplicada();
            Boolean repassaPg = pg.getTaxaRepassada();

            // Fallback
            if (taxaPg == null && pg.getFormaPagamento() != null) {
                taxaPg = pg.getFormaPagamento().getTaxa();
            }
            if (repassaPg == null && pg.getFormaPagamento() != null) {
                repassaPg = pg.getFormaPagamento().getRepassarTaxa();
            }

            // Calcular Líquido do Pagamento
            BigDecimal liquidoPg = calcularValorLiquido(pg.getValor(), taxaPg, repassaPg);

            // Dados visuais
            LocalDate dataPg = (pg.getAgendamento() != null) ? pg.getAgendamento().getData() : LocalDate.now();
            String nomeCliente = (pg.getAgendamento() != null && pg.getAgendamento().getClientes() != null)
                    ? pg.getAgendamento().getClientes().getNome()
                    : "Cliente Avulso";
            String nomeForma = (pg.getFormaPagamento() != null) ? pg.getFormaPagamento().getNome() : "Pagamento";

            extrato.add(new MovimentacaoFinanceiraDTO(
                    pg.getId(),
                    dataPg,
                    "Entrada (Restante) - " + nomeCliente,
                    liquidoPg,
                    "ENTRADA",
                    nomeForma));
        }

        // =========================================================================
        // 3. PROCESSAR DESPESAS (Saídas)
        // =========================================================================
        List<DespesasModel> despesas = despesasRepository.findAll();

        for (DespesasModel dp : despesas) {
            extrato.add(new MovimentacaoFinanceiraDTO(
                    dp.getId(),
                    dp.getDataDespesa(),
                    dp.getDescricao(),
                    dp.getValorTotal(),
                    "SAIDA",
                    dp.getCategoria() != null ? dp.getCategoria() : "GERAL"));
        }

        // 4. ORDENAR POR DATA (Mais recente no topo)
        extrato.sort(Comparator.comparing(MovimentacaoFinanceiraDTO::getData).reversed());

        return extrato;
    }

    // --- FUNÇÃO AUXILIAR DE CÁLCULO ---
    private BigDecimal calcularValorLiquido(BigDecimal valorBruto, BigDecimal taxa, Boolean repassada) {
        if (valorBruto == null)
            return BigDecimal.ZERO;

        // Garante valores não nulos para cálculo
        BigDecimal taxaSafe = (taxa != null) ? taxa : BigDecimal.ZERO;
        boolean repassadaSafe = (repassada != null) ? repassada : false;

        // Se taxa é zero, retorna bruto
        if (taxaSafe.compareTo(BigDecimal.ZERO) <= 0) {
            return valorBruto;
        }

        // Se repassada = TRUE (Cliente pagou a taxa a mais), para o caixa entra o valor
        // bruto cheio
        if (repassadaSafe) {
            return valorBruto;
        }

        // Se repassada = FALSE (Estabelecimento paga), desconta do valor bruto
        BigDecimal desconto = valorBruto
                .multiply(taxaSafe)
                .divide(new BigDecimal(100), 2, RoundingMode.HALF_UP);

        return valorBruto.subtract(desconto);
    }
}