package br.com.transporte.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Motorista extends Pessoa {
    private final String cnh;
    private boolean disponivel;
    private final Veiculo veiculo;
    private final List<Avaliacao> avaliacoes = new ArrayList<>();

    public Motorista(String nome, String cpf, String telefone, String cnh, Veiculo veiculo) {
        super(nome, cpf, telefone);
        this.cnh = validarTexto(cnh, "CNH");
        if (veiculo == null) {
            throw new IllegalArgumentException("O veículo é obrigatório");
        }
        this.veiculo = veiculo;
    }

    public void ficarDisponivel() {
        disponivel = true;
    }

    public void ficarIndisponivel() {
        disponivel = false;
    }

    public double calcularMediaAvaliacoes() {
        if (avaliacoes.isEmpty()) {
            return 0.0;
        }

        int soma = 0;
        for (Avaliacao avaliacao : avaliacoes) {
            soma = soma + avaliacao.getNota();
        }
        return (double) soma / avaliacoes.size();
    }

    void adicionarAvaliacao(Avaliacao avaliacao) {
        if (avaliacao == null) {
            throw new IllegalArgumentException("A avaliação é obrigatória");
        }
        avaliacoes.add(avaliacao);
    }

    public String getCnh() {
        return cnh;
    }

    public boolean isDisponivel() {
        return disponivel;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public List<Avaliacao> getAvaliacoes() {
        return Collections.unmodifiableList(avaliacoes);
    }

    @Override
    public String toString() {
        String situacao;
        if (disponivel) {
            situacao = "disponível";
        } else {
            situacao = "indisponível";
        }

        return String.format("%s | CPF: %s | CNH: %s | %s | %s",
                getNome(), getCpf(), cnh, veiculo, situacao);
    }
}
