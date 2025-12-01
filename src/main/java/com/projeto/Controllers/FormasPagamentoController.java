package com.projeto.Controllers;

import com.projeto.Models.FormasPagamentoModel;
import com.projeto.Repository.FormasPagamentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/formaspagamento")
public class FormasPagamentoController {

    @Autowired
    private FormasPagamentoRepository repository;

    @GetMapping
    public List<FormasPagamentoModel> listar() {
        return repository.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FormasPagamentoModel criar(@RequestBody FormasPagamentoModel forma) {
        return repository.save(forma);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FormasPagamentoModel> atualizar(@PathVariable Long id, @RequestBody FormasPagamentoModel dados) {
        return repository.findById(id)
                .map(existente -> {
                    existente.setNome(dados.getNome());
                    existente.setTaxa(dados.getTaxa());
                    existente.setRepassarTaxa(dados.getRepassarTaxa());
                    FormasPagamentoModel atualizado = repository.save(existente);
                    return ResponseEntity.ok(atualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deletar(@PathVariable Long id) {
        repository.deleteById(id);
    }
}