package br.com.transporte.model;

public abstract class Veiculo implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final String placa;
    private final String modelo;

    protected Veiculo(String placa, String modelo) {
        this.placa = Pessoa.validarTexto(placa, "placa").toUpperCase();
        this.modelo = Pessoa.validarTexto(modelo, "modelo");
    }

    public abstract double calcularTarifa(double distancia);

    /** 1 = Econômica, 2 = Conforto e 3 = Premium. */
    public abstract int getNivelCategoriaMaximo();

    protected double calcularComBase(double distancia, double tarifaBase, double porKm) {
        if (!Double.isFinite(distancia) || distancia <= 0) {
            throw new IllegalArgumentException("A distância deve ser maior que zero");
        }
        return tarifaBase + distancia * porKm;
    }

    public String getPlaca() {
        return placa;
    }

    public String getModelo() {
        return modelo;
    }

    @Override
    public String toString() {
        return String.format("%s - %s (%s)", placa, modelo, getClass().getSimpleName());
    }
}
