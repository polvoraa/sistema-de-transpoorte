package br.com.transporte.model;

public abstract class Pessoa {
    private final String nome;
    private final String cpf;
    private String telefone;

    protected Pessoa(String nome, String cpf, String telefone) {
        this.nome = validarTexto(nome, "nome");
        this.cpf = validarTexto(cpf, "CPF");
        this.telefone = validarTexto(telefone, "telefone");
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = validarTexto(telefone, "telefone");
    }

    protected static String validarTexto(String valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " não pode ser nulo");
        }
        if (valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode estar vazio");
        }
        return valor.trim();
    }
}
