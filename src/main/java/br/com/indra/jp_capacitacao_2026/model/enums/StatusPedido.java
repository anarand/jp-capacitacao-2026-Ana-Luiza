package br.com.indra.jp_capacitacao_2026.model.enums;

import java.util.Set;

/**
 * Maquina de estados do Pedido.
 * Cada status define quais transicoes sao permitidas.
 *
 * Fluxo normal:    CRIADO -> PAGO -> ENVIADO -> ENTREGUE
 * Cancelamento:    CRIADO -> CANCELADO
 *                  PAGO   -> CANCELADO
 *
 * Regra: nao e possivel cancelar um pedido ENVIADO ou ENTREGUE.
 */
public enum StatusPedido {

    CRIADO(Set.of("PAGO", "CANCELADO")),
    PAGO(Set.of("ENVIADO", "CANCELADO")),
    ENVIADO(Set.of("ENTREGUE")),
    ENTREGUE(Set.of()),
    CANCELADO(Set.of());

    private final Set<String> transicoesPermitidas;

    StatusPedido(Set<String> transicoesPermitidas) {
        this.transicoesPermitidas = transicoesPermitidas;
    }

    public boolean podeTransicionarPara(StatusPedido novoStatus) {
        return transicoesPermitidas.contains(novoStatus.name());
    }

    public boolean podeCancelar() {
        return transicoesPermitidas.contains("CANCELADO");
    }
}
