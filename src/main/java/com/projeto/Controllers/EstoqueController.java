package com.projeto.Controllers;

import com.projeto.Models.EstoqueModel;
import com.projeto.Repository.EstoqueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/estoque")
public class EstoqueController {

    @Autowired
    private EstoqueRepository repository;

    // Listar tudo
    @GetMapping
    public List<EstoqueModel> listar() {
        return repository.findAll();
    }

    // Adicionar Item Manualmente
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EstoqueModel criar(@RequestBody EstoqueModel item) {
        item.setDataUltimaAtualizacao(LocalDate.now());
        return repository.save(item);
    }

    // Atualizar (Ex: Baixa de estoque ou Correção)
    @PutMapping("/{id}")
    public ResponseEntity<EstoqueModel> atualizar(@PathVariable Long id, @RequestBody EstoqueModel dados) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNomeItem(dados.getNomeItem());
                    existente.setQuantidadeAtual(dados.getQuantidadeAtual());
                    existente.setQuantidadeMinima(dados.getQuantidadeMinima());
                    existente.setUnidade(dados.getUnidade());
                    existente.setCustoUltimaCompra(dados.getCustoUltimaCompra());
                    existente.setDataUltimaAtualizacao(LocalDate.now());

                    return ResponseEntity.ok(repository.save(existente));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        repository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}