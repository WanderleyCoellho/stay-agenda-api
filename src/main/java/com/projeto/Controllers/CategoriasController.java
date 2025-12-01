package com.projeto.Controllers;

import com.projeto.Models.CategoriasModel;
import com.projeto.Repository.CategoriasRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/categorias") 
public class CategoriasController {

    @Autowired
    private CategoriasRepository categoriaRepository;

    
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriasModel criarCategoria(@RequestBody CategoriasModel categoria) {
        return categoriaRepository.save(categoria);
    }

    
    @GetMapping
    public List<CategoriasModel> listarCategorias() {
        return categoriaRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<CategoriasModel> buscarCategoriaPorId(@PathVariable Long id) {
        Optional<CategoriasModel> categoria = categoriaRepository.findById(id);

        if (categoria.isPresent()) {
            return ResponseEntity.ok(categoria.get());
        }
        
        return ResponseEntity.notFound().build();
    }


    @PutMapping("/{id}")
    public ResponseEntity<CategoriasModel> atualizarCategoria(@PathVariable Long id, @RequestBody CategoriasModel categoriaDetalhes) {
        
        Optional<CategoriasModel> categoriaOptional = categoriaRepository.findById(id);

        if (categoriaOptional.isEmpty()) {
            return ResponseEntity.notFound().build(); 
        }

        CategoriasModel categoriaExistente = categoriaOptional.get();
        
        categoriaExistente.setCategoria(categoriaDetalhes.getCategoria()); 
        
        categoriaExistente.setDescricao(categoriaDetalhes.getDescricao());

        CategoriasModel categoriaAtualizada = categoriaRepository.save(categoriaExistente);
        return ResponseEntity.ok(categoriaAtualizada); 
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarCategoria(@PathVariable Long id) {
        
        if (!categoriaRepository.existsById(id)) {
            return ResponseEntity.notFound().build(); 
        }

        categoriaRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}