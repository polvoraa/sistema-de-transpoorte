package br.com.transporte.exception;

public class DistanciaInvalidaException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public DistanciaInvalidaException(String mensagem) {
        super(mensagem);
    }
}
