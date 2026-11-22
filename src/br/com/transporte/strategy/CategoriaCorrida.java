package br.com.transporte.strategy;

import br.com.transporte.model.Veiculo;

public interface CategoriaCorrida extends java.io.Serializable {
    double aplicarAcrescimo(double valor);

    int getNivelMinimoVeiculo();

    default boolean isCompativel(Veiculo veiculo) {
        return veiculo != null && veiculo.getNivelCategoriaMaximo() >= getNivelMinimoVeiculo();
    }

    default String getNome() {
        return getClass().getSimpleName();
    }
}
