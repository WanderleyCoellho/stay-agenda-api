package com.projeto.Controllers;

import com.projeto.Models.PromocoesModel;
import com.projeto.Repository.PromocoesRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/promocoes") 
public class PromocoesController {

    @Autowired
    private PromocoesRepository promocaoRepository;

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromocoesModel criarPromocao(@RequestBody PromocoesModel promocao) {
        if (promocao.getStatus() == null) {
            promocao.setStatus(PromocoesModel.StatusPromocao.ATIVA);
        }
        return promocaoRepository.save(promocao);
    }

    @GetMapping
    public List<PromocoesModel> listarPromocoes() {
        return promocaoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<PromocoesModel> buscarPromocaoPorId(@PathVariable Long id) {
        Optional<PromocoesModel> promocao = promocaoRepository.findById(id);

        if (promocao.isPresent()) {
            return ResponseEntity.ok(promocao.get());
        }
        else {
        return ResponseEntity.notFound().build();}
    }

    @PutMapping("/{id}")
    public ResponseEntity<PromocoesModel> atualizarPromocao(@PathVariable Long id, @RequestBody PromocoesModel promocaoDetalhes) {
        
        if (!promocaoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        promocaoDetalhes.setId(id);
        return ResponseEntity.ok(promocaoRepository.save(promocaoDetalhes));
    }

  
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarPromocao(@PathVariable Long id) {
        
        if (!promocaoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        promocaoRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}