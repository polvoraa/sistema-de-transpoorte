package br.com.transporte.exception;

public class MotoristaIndisponivelException extends IllegalStateException {
    private static final long serialVersionUID = 1L;

    public MotoristaIndisponivelException(String mensagem) {
        super(mensagem);
    }
}
