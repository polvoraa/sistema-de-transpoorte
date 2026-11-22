package br.com.transporte.strategy;

public class Pix implements FormaPagamento {
    private static final long serialVersionUID = 1L;
    public static final double DESCONTO = 0.05;

    @Override
    public double aplicar(double valor) {
        return valor * (1 - DESCONTO);
    }
}
