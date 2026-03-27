package br.com.indra.jp_capacitacao_2026.controller;

import br.com.indra.jp_capacitacao_2026.model.enums.StatusPedido;
import br.com.indra.jp_capacitacao_2026.service.PedidoService;
import br.com.indra.jp_capacitacao_2026.service.dto.PedidoDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Pedidos", description = "Endpoints para gerenciamento de pedidos")
@RequestMapping("/orders")
public class PedidoController {

    private final PedidoService pedidoService;

    @Operation(summary = "Realizar checkout: converte carrinho ativo em pedido e desconta estoque")
    @PostMapping
    public ResponseEntity<PedidoDTO> checkout(
            @RequestHeader("X-User-Id") String userId,
            @RequestParam(required = false) String endereco) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.checkout(userId, endereco));
    }

    @Operation(summary = "Buscar pedido por ID")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.getById(id));
    }

    @Operation(summary = "Listar todos os pedidos do usuario")
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getMeusPedidos(@RequestHeader("X-User-Id") String userId) {
        return ResponseEntity.ok(pedidoService.getPedidosByUsuario(userId));
    }

    @Operation(summary = "Cancelar pedido (apenas status CRIADO ou PAGO — devolve estoque automaticamente)")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<PedidoDTO> cancelar(@PathVariable Long id) {
        return ResponseEntity.ok(pedidoService.cancelar(id));
    }

    @Operation(summary = "Atualizar status do pedido (CRIADO -> PAGO -> ENVIADO -> ENTREGUE)")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDTO> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusPedido status) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, status));
    }
}
