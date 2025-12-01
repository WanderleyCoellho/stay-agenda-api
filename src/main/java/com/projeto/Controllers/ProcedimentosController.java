package com.projeto.Controllers;

import com.projeto.Models.ProcedimentosModel;
import com.projeto.Repository.ProcedimentosRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/procedimentos")
public class ProcedimentosController {

    @Autowired
    private ProcedimentosRepository procedimentoRepository;

   
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedimentosModel criarProcedimento(@RequestBody ProcedimentosModel procedimento) {
        return procedimentoRepository.save(procedimento);
    }


    @GetMapping
    public List<ProcedimentosModel> listarProcedimentos() {
        return procedimentoRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<ProcedimentosModel> buscarProcedimentoPorId(@PathVariable Long id) {
        Optional<ProcedimentosModel> procedimento = procedimentoRepository.findById(id);

        if (procedimento.isPresent()) {
            return ResponseEntity.ok(procedimento.get());
        }
        
        return ResponseEntity.notFound().build();
    }

   
    @PutMapping("/{id}")
    public ResponseEntity<ProcedimentosModel> atualizarProcedimento(@PathVariable Long id, @RequestBody ProcedimentosModel procedimentoDetalhes) {
        
        Optional<ProcedimentosModel> procedimentoOptional = procedimentoRepository.findById(id);

        if (procedimentoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        ProcedimentosModel procedimentoExistente = procedimentoOptional.get();
        
        
        procedimentoExistente.setProcedimento(procedimentoDetalhes.getProcedimento());
        procedimentoExistente.setDescricao(procedimentoDetalhes.getDescricao());
        procedimentoExistente.setValor(procedimentoDetalhes.getValor());
        
       
        procedimentoExistente.setCategoria(procedimentoDetalhes.getCategoria());

        ProcedimentosModel procedimentoAtualizado = procedimentoRepository.save(procedimentoExistente);
        
        return ResponseEntity.ok(procedimentoAtualizado);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarProcedimento(@PathVariable Long id) {
        
        if (!procedimentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        procedimentoRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}