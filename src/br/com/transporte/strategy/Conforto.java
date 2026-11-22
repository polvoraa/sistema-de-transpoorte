package br.com.transporte.strategy;

public class Conforto implements CategoriaCorrida {
    private static final long serialVersionUID = 1L;
    public static final double ACRESCIMO = 0.20;

    @Override
    public double aplicarAcrescimo(double valor) {
        return valor * (1 + ACRESCIMO);
    }

    @Override
    public int getNivelMinimoVeiculo() { return 2; }
}
