package br.com.transporte.strategy;

public class Economica implements CategoriaCorrida {
    private static final long serialVersionUID = 1L;
    @Override
    public double aplicarAcrescimo(double valor) {
        return valor;
    }

    @Override
    public int getNivelMinimoVeiculo() { return 1; }
}
