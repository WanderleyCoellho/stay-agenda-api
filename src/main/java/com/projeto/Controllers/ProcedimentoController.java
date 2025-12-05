package com.projeto.Controllers;

import com.projeto.Models.MateriaisModel;
import com.projeto.Models.ProcedimentosModel;
import com.projeto.Repository.ProcedimentosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/procedimentos")
// @CrossOrigin(origins = "*") // Não precisa mais (já temos global)
public class ProcedimentoController {

    @Autowired
    private ProcedimentosRepository repository;

    @GetMapping
    public List<ProcedimentosModel> listar() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProcedimentosModel> buscarPorId(@PathVariable Long id) {
        return repository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedimentosModel criar(@RequestBody ProcedimentosModel procedimento) {
        // --- VÍNCULO DOS MATERIAIS (PRECIFICAÇÃO) ---
        if (procedimento.getMateriais() != null) {
            for (MateriaisModel m : procedimento.getMateriais()) {
                m.setProcedimento(procedimento); // <--- O PULO DO GATO
            }
        }
        return repository.save(procedimento);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProcedimentosModel> atualizar(@PathVariable Long id, @RequestBody ProcedimentosModel dados) {
        return repository.findById(id)
                .map(existente -> {
                    // Atualiza campos básicos
                    existente.setProcedimento(dados.getProcedimento());
                    existente.setValor(dados.getValor());

                    if (dados.getValorManual() != null) {
                        existente.setValorManual(dados.getValorManual());
                    }

                    existente.setCategoria(dados.getCategoria());

                    // Atualiza campos de Precificação
                    existente.setTempoMinutos(dados.getTempoMinutos());
                    existente.setMargemLucro(dados.getMargemLucro());
                    existente.setUsarPrecificacao(dados.getUsarPrecificacao());

                    // --- ATUALIZA A LISTA DE MATERIAIS ---
                    // 1. Limpa a lista antiga (o orphanRemoval=true apaga do banco)
                    existente.getMateriais().clear();

                    // 2. Adiciona os novos com o vínculo correto
                    if (dados.getMateriais() != null) {
                        for (MateriaisModel m : dados.getMateriais()) {
                            m.setProcedimento(existente); // Vínculo essencial
                            existente.getMateriais().add(m);
                        }
                    }

                    ProcedimentosModel atualizado = repository.save(existente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}