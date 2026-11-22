package br.com.transporte.exception;

public class VeiculoIncompativelException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public VeiculoIncompativelException(String mensagem) {
        super(mensagem);
    }
}
