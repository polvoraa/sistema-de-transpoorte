package br.com.transporte.exception;

public class CadastroDuplicadoException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public CadastroDuplicadoException(String mensagem) {
        super(mensagem);
    }
}
