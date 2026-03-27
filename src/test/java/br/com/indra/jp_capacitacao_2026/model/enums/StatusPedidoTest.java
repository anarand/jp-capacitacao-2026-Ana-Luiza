package br.com.indra.jp_capacitacao_2026.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para a maquina de estados do Pedido.
 * Valida que apenas transicoes permitidas sao aceitas.
 */
class StatusPedidoTest {

    @Test
    void criadoPodeTransicionarParaPago() {
        assertTrue(StatusPedido.CRIADO.podeTransicionarPara(StatusPedido.PAGO));
    }

    @Test
    void criadoPodeSerCancelado() {
        assertTrue(StatusPedido.CRIADO.podeCancelar());
    }

    @Test
    void pagoPodeTransicionarParaEnviado() {
        assertTrue(StatusPedido.PAGO.podeTransicionarPara(StatusPedido.ENVIADO));
    }

    @Test
    void pagoPodeSerCancelado() {
        assertTrue(StatusPedido.PAGO.podeCancelar());
    }

    @Test
    void enviadoNaoPodeSerCancelado() {
        assertFalse(StatusPedido.ENVIADO.podeCancelar());
    }

    @Test
    void enviadoPodeTransicionarParaEntregue() {
        assertTrue(StatusPedido.ENVIADO.podeTransicionarPara(StatusPedido.ENTREGUE));
    }

    @Test
    void entregueNaoPodeTransicionarParaNenhumStatus() {
        assertFalse(StatusPedido.ENTREGUE.podeTransicionarPara(StatusPedido.CANCELADO));
        assertFalse(StatusPedido.ENTREGUE.podeTransicionarPara(StatusPedido.PAGO));
        assertFalse(StatusPedido.ENTREGUE.podeCancelar());
    }

    @Test
    void criadoNaoPodeIrDiretoParaEntregue() {
        assertFalse(StatusPedido.CRIADO.podeTransicionarPara(StatusPedido.ENTREGUE));
    }
}
