package br.com.transporte.strategy;

public class Dinheiro implements FormaPagamento {
    private static final long serialVersionUID = 1L;
    @Override
    public double aplicar(double valor) {
        return valor;
    }
}
