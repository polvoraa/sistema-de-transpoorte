package br.com.transporte.strategy;

public class Pix implements FormaPagamento {
    public static final double DESCONTO = 0.05;

    @Override
    public double aplicar(double valor) {
        return valor * (1 - DESCONTO);
    }
}
