package br.com.transporte.strategy;

public class Cartao implements FormaPagamento {
    public static final double TAXA = 0.03;

    @Override
    public double aplicar(double valor) {
        return valor * (1 + TAXA);
    }
}
