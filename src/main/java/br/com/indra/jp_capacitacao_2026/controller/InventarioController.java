package br.com.indra.jp_capacitacao_2026.controller;

import br.com.indra.jp_capacitacao_2026.model.Produtos;
import br.com.indra.jp_capacitacao_2026.service.InventarioService;
import br.com.indra.jp_capacitacao_2026.service.dto.InventarioRequest;
import br.com.indra.jp_capacitacao_2026.service.dto.TransacaoEstoqueDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Inventario", description = "Endpoints para controle de estoque")
@RequestMapping("/inventory")
public class InventarioController {

    private final InventarioService inventarioService;

    @Operation(summary = "Adicionar estoque a um produto (ENTRADA)")
    @PostMapping("/{produtoId}/add")
    public ResponseEntity<TransacaoEstoqueDTO> adicionar(
            @PathVariable Long produtoId,
            @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.adicionarEstoque(
                produtoId, request.getQuantidade(), request.getMotivo(), request.getCriadoPor()));
    }

    @Operation(summary = "Remover estoque de um produto (SAIDA) — retorna erro se insuficiente")
    @PostMapping("/{produtoId}/remove")
    public ResponseEntity<TransacaoEstoqueDTO> remover(
            @PathVariable Long produtoId,
            @RequestBody InventarioRequest request) {
        return ResponseEntity.ok(inventarioService.removerEstoque(
                produtoId, request.getQuantidade(), request.getMotivo(), request.getCriadoPor()));
    }

    @Operation(summary = "Historico de movimentacoes de estoque de um produto")
    @GetMapping("/{produtoId}")
    public ResponseEntity<List<TransacaoEstoqueDTO>> getHistorico(@PathVariable Long produtoId) {
        return ResponseEntity.ok(inventarioService.getHistorico(produtoId));
    }

    @Operation(summary = "Listar produtos com estoque baixo (abaixo de 5 unidades)")
    @GetMapping("/baixo-estoque")
    public ResponseEntity<List<Produtos>> getEstoqueBaixo() {
        return ResponseEntity.ok(inventarioService.getProdutosComEstoqueBaixo());
    }
}
