package com.projeto.Controllers;

import com.projeto.dto.MovimentacaoFinanceiraDTO;
import com.projeto.Services.FinanceiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/financeiro")
public class FinanceiroController {

    @Autowired
    private FinanceiroService financeiroService;

    @GetMapping("/extrato")
    public ResponseEntity<List<MovimentacaoFinanceiraDTO>> getExtrato() {
        List<MovimentacaoFinanceiraDTO> extrato = financeiroService.gerarExtratoUnificado();
        return ResponseEntity.ok(extrato);
    }
}