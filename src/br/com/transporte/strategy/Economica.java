package br.com.transporte.strategy;

public class Economica implements CategoriaCorrida {
    @Override
    public double aplicarAcrescimo(double valor) {
        return valor;
    }

    @Override
    public int getNivelMinimoVeiculo() { return 1; }
}
