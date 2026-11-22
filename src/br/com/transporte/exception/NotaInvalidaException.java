package br.com.transporte.exception;

public class NotaInvalidaException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public NotaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
