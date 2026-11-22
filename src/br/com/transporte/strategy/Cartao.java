package br.com.transporte.strategy;

public class Cartao implements FormaPagamento {
    private static final long serialVersionUID = 1L;
    public static final double TAXA = 0.03;

    @Override
    public double aplicar(double valor) {
        return valor * (1 + TAXA);
    }
}
