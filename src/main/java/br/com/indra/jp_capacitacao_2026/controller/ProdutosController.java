package br.com.indra.jp_capacitacao_2026.controller;

import br.com.indra.jp_capacitacao_2026.model.Produtos;
import br.com.indra.jp_capacitacao_2026.service.HistoricoService;
import br.com.indra.jp_capacitacao_2026.service.ProdutosService;
import br.com.indra.jp_capacitacao_2026.service.dto.HistoricoProdutoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * CORRECOES APLICADAS:
 * - Removido import @Log4j (estava importado mas nao utilizado)
 * - DELETE agora e logico (produto marcado como inativo, nao excluido)
 * - PUT /atualiza recebe id via @PathVariable (antes pegava por @RequestParam mas nao usava)
 * - Adicionado endpoint de busca por nome e por categoria
 * - Adicionado endpoint de historico de precos
 * - Respostas HTTP mais semanticas (201 Created para POST)
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Produtos", description = "Endpoints para gerenciamento de produtos")
@RequestMapping("/produtos")
public class ProdutosController {

    private final ProdutosService produtosService;
    private final HistoricoService historicoService;

    @Operation(summary = "Criar novo produto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados invalidos")
    })
    @PostMapping("/cria")
    public ResponseEntity<Produtos> criarProduto(@RequestBody Produtos produto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtosService.createdProduto(produto));
    }

    @Operation(summary = "Listar todos os produtos ativos")
    @GetMapping
    public ResponseEntity<List<Produtos>> getAll() {
        return ResponseEntity.ok(produtosService.getAll());
    }

    @Operation(summary = "Buscar produto por ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Produto encontrado"),
        @ApiResponse(responseCode = "404", description = "Produto nao encontrado")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Produtos> getById(@PathVariable Long id) {
        return ResponseEntity.ok(produtosService.getById(id));
    }

    @Operation(summary = "Buscar produtos por nome (parcial, case-insensitive)")
    @GetMapping("/busca")
    public ResponseEntity<List<Produtos>> buscarPorNome(@RequestParam String nome) {
        return ResponseEntity.ok(produtosService.buscarPorNome(nome));
    }

    @Operation(summary = "Buscar produtos por categoria")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Produtos>> buscarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(produtosService.buscarPorCategoria(categoriaId));
    }

    @Operation(summary = "Atualizar produto completo")
    @PutMapping("/atualiza/{id}")
    public ResponseEntity<Produtos> atualizarProduto(@PathVariable Long id,
                                                      @RequestBody Produtos produto) {
        return ResponseEntity.ok(produtosService.atualiza(id, produto));
    }

    @Operation(summary = "Atualizar apenas o preco do produto (registra historico automaticamente)")
    @PatchMapping("/atualiza-preco/{id}")
    public ResponseEntity<Produtos> atualizarPreco(@PathVariable Long id,
                                                    @RequestParam BigDecimal preco) {
        return ResponseEntity.ok(produtosService.atualizaPreco(id, preco));
    }

    @Operation(summary = "Delete logico: marca produto como inativo (nao remove do banco)")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Produto desativado"),
        @ApiResponse(responseCode = "404", description = "Produto nao encontrado")
    })
    @DeleteMapping("/deleta/{id}")
    public ResponseEntity<Void> deletarProduto(@PathVariable Long id) {
        produtosService.deletarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar historico de precos de um produto")
    @GetMapping("/{id}/historico-precos")
    public ResponseEntity<List<HistoricoProdutoDTO>> getHistoricoPrecos(@PathVariable Long id) {
        return ResponseEntity.ok(historicoService.getHistoricoByProdutoId(id));
    }
}
