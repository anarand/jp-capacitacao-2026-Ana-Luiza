package br.com.indra.jp_capacitacao_2026.exception;

/**
 * Lançada quando uma regra de negócio é violada (ex: cancelar pedido entregue).
 * Resulta em HTTP 400 via GlobalExceptionHandler.
 */
public class RegraDeNegocioException extends RuntimeException {
    public RegraDeNegocioException(String message) {
        super(message);
    }
}
