package br.com.transporte.strategy;

public class Premium implements CategoriaCorrida {
    private static final long serialVersionUID = 1L;
    public static final double ACRESCIMO = 0.50;

    @Override
    public double aplicarAcrescimo(double valor) {
        return valor * (1 + ACRESCIMO);
    }

    @Override
    public int getNivelMinimoVeiculo() { return 3; }
}
