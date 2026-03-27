package br.com.indra.jp_capacitacao_2026.exception;

/**
 * Lançada quando um recurso não é encontrado no banco de dados.
 * Resulta em HTTP 404 via GlobalExceptionHandler.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
