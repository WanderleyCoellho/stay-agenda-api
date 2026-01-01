package com.projeto.Controllers;

import com.projeto.Models.AgendamentosModel;
import com.projeto.Models.PagamentosModel; // <--- Importante
import com.projeto.Repositories.AgendamentosRepository;
import com.projeto.Repositories.FormasPagamentoRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/agendamentos")
public class AgendamentosController {

    @Autowired
    private AgendamentosRepository agendamentoRepository;
    @Autowired
    private FormasPagamentoRepository formasPagamentoRepository;

    private void preencherSnapshotFinanceiro(AgendamentosModel ag) {
        // 1. Snapshot do Sinal
        if (ag.getFormaPagamentoSinal() != null && ag.getFormaPagamentoSinal().getId() != null) {
            // Busca no banco a forma de pagamento atualizada
            formasPagamentoRepository.findById(ag.getFormaPagamentoSinal().getId()).ifPresent(forma -> {
                ag.setTaxaSinalAplicada(forma.getTaxa());
                ag.setTaxaSinalRepassada(forma.getRepassarTaxa());
            });
        }

        // 2. Snapshot da Lista de Pagamentos
        if (ag.getPagamentos() != null) {
            for (PagamentosModel pg : ag.getPagamentos()) {
                pg.setAgendamento(ag); // Garante o vínculo

                if (pg.getFormaPagamento() != null && pg.getFormaPagamento().getId() != null) {
                    formasPagamentoRepository.findById(pg.getFormaPagamento().getId()).ifPresent(forma -> {
                        pg.setTaxaAplicada(forma.getTaxa());
                        pg.setTaxaRepassada(forma.getRepassarTaxa());
                    });
                }
            }
        }
    }

    // --- CRIAR (POST) ---
    @PostMapping
    public ResponseEntity<?> criarAgendamento(@RequestBody AgendamentosModel agendamento) {

        // 1. VALIDAÇÃO DE HORÁRIO DUPLICADO
        boolean jaExiste = agendamentoRepository.existsByDataAndHoraInicialAndStatusNot(
                agendamento.getData(),
                agendamento.getHoraInicial(),
                "CANCELADO");

        if (jaExiste) {
            // Retorna Erro 409 (Conflict) com mensagem
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Erro: Já existe um cliente agendado para este horário!");
        }

        // 2. VÍNCULO DOS PAGAMENTOS (Lista)
        if (agendamento.getPagamentos() != null) {
            for (com.projeto.Models.PagamentosModel pg : agendamento.getPagamentos()) {
                pg.setAgendamento(agendamento);
            }
        }

        // 3. SNAPSHOT FINANCEIRO
        preencherSnapshotFinanceiro(agendamento);

        // 4. SALVAR
        AgendamentosModel salvo = agendamentoRepository.save(agendamento);

        // Retorna Sucesso 201 (Created) com o objeto salvo
        return ResponseEntity.status(HttpStatus.CREATED).body(salvo);
    }

    // --- FILTRO POR DATA ---
    @GetMapping("/filtro")
    public List<AgendamentosModel> listarPorData(
            @RequestParam(value = "data", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data) {
        if (data == null) {
            data = LocalDate.now();
        }
        return agendamentoRepository.findByDataOrderByHoraInicialAsc(data);
    }

    // --- LISTAR TODOS ---
    @GetMapping
    public List<AgendamentosModel> listarAgendamentos() {
        return agendamentoRepository.findAll();
    }

    // --- BUSCAR POR ID ---
    @GetMapping("/{id}")
    public ResponseEntity<AgendamentosModel> buscarAgendamentoPorId(@PathVariable Long id) {
        Optional<AgendamentosModel> agendamento = agendamentoRepository.findById(id);
        return agendamento.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // --- ATUALIZAR (PUT) ---
    @PutMapping("/{id}")
    public ResponseEntity<AgendamentosModel> atualizarAgendamento(@PathVariable Long id,
            @RequestBody AgendamentosModel novosDados) {

        Optional<AgendamentosModel> agendamentoOptional = agendamentoRepository.findById(id);

        if (agendamentoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        AgendamentosModel existente = agendamentoOptional.get();

        // Atualiza campos simples
        existente.setData(novosDados.getData());
        existente.setHoraInicial(novosDados.getHoraInicial());
        existente.setHoraFinal(novosDados.getHoraFinal());
        existente.setObservacoes(novosDados.getObservacoes());
        existente.setStatus(novosDados.getStatus());

        existente.setValorParcial(novosDados.getValorParcial());
        existente.setValorProcedimento(novosDados.getValorProcedimento());

        // CORREÇÃO: Atualiza o existente com o novo dado
        existente.setValorDesconto(novosDados.getValorDesconto());

        // Atualiza relacionamentos simples
        existente.setClientes(novosDados.getClientes());
        existente.setProcedimentos(novosDados.getProcedimentos());
        existente.setPromocao(novosDados.getPromocao());

        // Forma de pagamento do SINAL (pode ser null se não tiver sinal)
        existente.setFormaPagamentoSinal(novosDados.getFormaPagamentoSinal());

        // --- ATUALIZAÇÃO DA LISTA DE PAGAMENTOS ---
        // 1. Limpa a lista antiga (o orphanRemoval=true no Model vai apagar do banco)
        existente.getPagamentos().clear();

        // 2. Adiciona os novos, refazendo o vínculo
        if (novosDados.getPagamentos() != null) {
            for (PagamentosModel pg : novosDados.getPagamentos()) {
                pg.setAgendamento(existente); // <--- Vínculo importante!
                existente.getPagamentos().add(pg);
            }
        }

        preencherSnapshotFinanceiro(existente);
        AgendamentosModel agendamentoAtualizado = agendamentoRepository.save(existente);
        return ResponseEntity.ok(agendamentoAtualizado);
    }

    // --- DELETAR ---
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarAgendamento(@PathVariable Long id) {
        if (!agendamentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        agendamentoRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}