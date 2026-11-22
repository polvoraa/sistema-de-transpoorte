package br.com.transporte.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.transporte.service.GerenciadorCorridas;
import br.com.transporte.strategy.CategoriaCorrida;
import br.com.transporte.strategy.FormaPagamento;

public class Passageiro extends Pessoa {
    private static final long serialVersionUID = 1L;
    private final List<Corrida> corridasSolicitadas = new ArrayList<>();

    public Passageiro(String nome, String cpf, String telefone) {
        super(nome, cpf, telefone);
    }

    public Corrida solicitarCorrida(GerenciadorCorridas gerenciador, String origem,
            String destino, double distancia, FormaPagamento formaPagamento,
            CategoriaCorrida categoria) {
        if (gerenciador == null) {
            throw new IllegalArgumentException("O gerenciador é obrigatório");
        }
        return gerenciador.solicitarCorrida(this, origem, destino, distancia,
                formaPagamento, categoria);
    }

    public List<Corrida> getCorridasSolicitadas() {
        return Collections.unmodifiableList(corridasSolicitadas);
    }

    public void registrarCorrida(Corrida corrida) {
        if (corrida == null || corrida.getPassageiro() != this) {
            throw new IllegalArgumentException("A corrida deve pertencer a este passageiro");
        }
        corridasSolicitadas.add(corrida);
    }

    @Override
    public String toString() {
        return String.format("%s | CPF: %s | telefone: %s", getNome(), getCpf(), getTelefone());
    }
}
