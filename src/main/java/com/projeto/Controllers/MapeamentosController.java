package com.projeto.Controllers;

import com.projeto.Models.ClientesModel;
import com.projeto.Models.MapeamentosModel;
import com.projeto.Models.ProcedimentosModel;
import com.projeto.Repository.MapeamentosRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/mapeamentos")
public class MapeamentosController {

    @Autowired
    private MapeamentosRepository mapeamentoRepository;

    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> criar(
            @RequestParam("arquivo") MultipartFile file,
            @RequestParam("descricao") String descricao,
            @RequestParam("clienteId") Long clienteId,
            @RequestParam("procedimentoId") Long procedimentoId,
            @RequestParam("agendamentoId" ) Long agendamentoId
            
    ) {
        try {
            MapeamentosModel mapa = new MapeamentosModel();
            mapa.setDescricao(descricao);
            mapa.setNomeArquivo(file.getOriginalFilename());
            mapa.setTipoArquivo(file.getContentType()); // O navegador precisa disso!
            mapa.setMidia(file.getBytes());

            // Vincula Cliente
            ClientesModel cli = new ClientesModel();
            cli.setId(clienteId);
            mapa.setClientes(cli);

            // Vincula Procedimento
            ProcedimentosModel proc = new ProcedimentosModel();
            proc.setId(procedimentoId);
            mapa.setProcedimentos(proc);

            // Vincula Agendamento
            mapa.setAgendamentos(new com.projeto.Models.AgendamentosModel());
            mapa.getAgendamentos().setId(agendamentoId);

            mapeamentoRepository.save(mapa);
            return ResponseEntity.status(HttpStatus.CREATED).body("Mapeamento salvo!");

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro no upload");
        }
    }

    
    @GetMapping
    public List<MapeamentosModel> listarMapeamentos() {
        return mapeamentoRepository.findAll();
    }

    
    @GetMapping("/{id}")
    public ResponseEntity<MapeamentosModel> buscarMapeamentoPorId(@PathVariable Long id) {
        Optional<MapeamentosModel> mapeamento = mapeamentoRepository.findById(id);

        if (mapeamento.isPresent()) {
            return ResponseEntity.ok(mapeamento.get());
        }
        
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<MapeamentosModel> listarPorCliente(@PathVariable Long clienteId) {
        List<MapeamentosModel> lista = mapeamentoRepository.findByClientesId(clienteId);
        
        return lista;
    }
   
    @PutMapping("/{id}")
    public ResponseEntity<MapeamentosModel> atualizarMapeamento(@PathVariable Long id, @RequestBody MapeamentosModel mapeamentoDetalhes) {
        
        Optional<MapeamentosModel> mapeamentoOptional = mapeamentoRepository.findById(id);

        if (mapeamentoOptional.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        MapeamentosModel mapeamentoExistente = mapeamentoOptional.get();
        
        mapeamentoExistente.setDescricao(mapeamentoDetalhes.getDescricao());
        mapeamentoExistente.setNomeArquivo(mapeamentoDetalhes.getNomeArquivo());
        mapeamentoExistente.setProcedimentos(mapeamentoDetalhes.getProcedimentos());
        mapeamentoExistente.setAgendamentos(mapeamentoDetalhes.getAgendamentos());
        mapeamentoExistente.setClientes(mapeamentoDetalhes.getClientes());

        MapeamentosModel mapeamentoAtualizado = mapeamentoRepository.save(mapeamentoExistente);
        return ResponseEntity.ok(mapeamentoAtualizado);
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletarMapeamento(@PathVariable Long id) {
        
        if (!mapeamentoRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        mapeamentoRepository.deleteById(id);
        
        return ResponseEntity.noContent().build();
    }
}