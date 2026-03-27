package br.com.indra.jp_capacitacao_2026.model.enums;

/**
 * Status do Carrinho:
 * - ATIVO: carrinho em uso pelo usuario
 * - FINALIZADO: convertido em pedido via checkout
 * - ABANDONADO: expirado sem checkout (util para relatorios futuros)
 */
public enum StatusCarrinho {
    ATIVO,
    FINALIZADO,
    ABANDONADO
}
