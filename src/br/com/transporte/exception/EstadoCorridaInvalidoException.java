package br.com.transporte.exception;

public class EstadoCorridaInvalidoException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public EstadoCorridaInvalidoException(String mensagem) {
        super(mensagem);
    }
}
