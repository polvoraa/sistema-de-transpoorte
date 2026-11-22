package br.com.transporte.strategy;

@FunctionalInterface
public interface FormaPagamento extends java.io.Serializable {
    double aplicar(double valor);

    default String getNome() {
        return getClass().getSimpleName();
    }
}
