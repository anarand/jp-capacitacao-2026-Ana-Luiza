package br.com.indra.jp_capacitacao_2026.exception;

/**
 * Lançada quando uma operação tenta usar mais estoque do que o disponível.
 * Resulta em HTTP 422 via GlobalExceptionHandler.
 */
public class EstoqueInsuficienteException extends RuntimeException {
    public EstoqueInsuficienteException(String message) {
        super(message);
    }
}
