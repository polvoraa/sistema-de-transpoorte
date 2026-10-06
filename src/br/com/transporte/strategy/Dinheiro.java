package br.com.transporte.strategy;

public class Dinheiro implements FormaPagamento {
    @Override
    public double aplicar(double valor) {
        return valor;
    }
}
