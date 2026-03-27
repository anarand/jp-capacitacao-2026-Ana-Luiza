package br.com.indra.jp_capacitacao_2026.model.enums;

/**
 * Tipos de transacao de estoque.
 * Cada ajuste de estoque gera um registro de TransacaoEstoque com um desses tipos.
 */
public enum TipoTransacao {
    ENTRADA,    // compra / reposicao de fornecedor
    SAIDA,      // venda / pedido finalizado
    AJUSTE,     // correcao manual de estoque
    DEVOLUCAO   // produto devolvido pelo cliente
}
