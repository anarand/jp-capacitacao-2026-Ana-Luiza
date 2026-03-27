package br.com.indra.jp_capacitacao_2026.controller;

import br.com.indra.jp_capacitacao_2026.service.CarrinhoService;
import br.com.indra.jp_capacitacao_2026.service.dto.CarrinhoDTO;
import br.com.indra.jp_capacitacao_2026.service.dto.ItemCarrinhoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * O userId e passado via @RequestHeader para simular autenticacao.
 * Em producao seria extraido do token JWT via Spring Security.
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Carrinho", description = "Endpoints para gerenciamento do carrinho de compras")
@RequestMapping("/cart")
public class CarrinhoController {

    private final CarrinhoService carrinhoService;

    @Operation(summary = "Buscar carrinho ativo do usuario (cria um novo se nao existir)")
    @GetMapping
    public ResponseEntity<CarrinhoDTO> getCarrinho(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(carrinhoService.getOuCriarCarrinho(userId));
    }

    @Operation(summary = "Adicionar item ao carrinho (incrementa se produto ja existir)")
    @PostMapping("/items")
    public ResponseEntity<CarrinhoDTO> adicionarItem(
            @RequestHeader("X-User-Id") String userId,
            @RequestBody ItemCarrinhoRequest request) {
        return ResponseEntity.ok(carrinhoService.adicionarItem(userId, request));
    }

    @Operation(summary = "Atualizar quantidade de um item (quantidade 0 remove o item)")
    @PutMapping("/items/{itemId}")
    public ResponseEntity<CarrinhoDTO> atualizarItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long itemId,
            @RequestParam Integer quantidade) {
        return ResponseEntity.ok(carrinhoService.atualizarItem(userId, itemId, quantidade));
    }

    @Operation(summary = "Remover item do carrinho")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CarrinhoDTO> removerItem(
            @RequestHeader("X-User-Id") String userId,
            @PathVariable Long itemId) {
        return ResponseEntity.ok(carrinhoService.removerItem(userId, itemId));
    }
}
