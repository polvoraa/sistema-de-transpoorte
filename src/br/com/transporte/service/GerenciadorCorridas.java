package br.com.transporte.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import br.com.transporte.model.Corrida;
import br.com.transporte.model.EstadoCorrida;
import br.com.transporte.model.Passageiro;
import br.com.transporte.strategy.CategoriaCorrida;
import br.com.transporte.strategy.FormaPagamento;

public class GerenciadorCorridas implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Corrida> corridas = new ArrayList<>();
    private int proximoId = 1;

    public Corrida solicitarCorrida(Passageiro passageiro, String origem, String destino,
            double distancia, FormaPagamento formaPagamento, CategoriaCorrida categoria) {
        if (passageiro == null) {
            throw new IllegalArgumentException("O passageiro é obrigatório");
        }
        Corrida corrida = new Corrida(proximoId, passageiro, origem,
                destino, distancia, formaPagamento, categoria);
        proximoId++;
        corridas.add(corrida);
        passageiro.registrarCorrida(corrida);
        return corrida;
    }

    public List<Corrida> listarPorEstado(EstadoCorrida estado) {
        if (estado == null) {
            throw new IllegalArgumentException("O estado é obrigatório");
        }
        List<Corrida> encontradas = new ArrayList<>();
        for (Corrida corrida : corridas) {
            if (corrida.getEstado() == estado) {
                encontradas.add(corrida);
            }
        }
        return encontradas;
    }

    public List<Corrida> getCorridas() {
        return Collections.unmodifiableList(corridas);
    }
}
