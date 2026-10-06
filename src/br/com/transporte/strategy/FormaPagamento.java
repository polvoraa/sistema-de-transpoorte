package br.com.transporte.strategy;

@FunctionalInterface
public interface FormaPagamento {
    double aplicar(double valor);

    default String getNome() {
        return getClass().getSimpleName();
    }
}
