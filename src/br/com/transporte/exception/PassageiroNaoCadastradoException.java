package br.com.transporte.exception;

public class PassageiroNaoCadastradoException extends IllegalArgumentException {
    private static final long serialVersionUID = 1L;

    public PassageiroNaoCadastradoException(String mensagem) {
        super(mensagem);
    }
}
