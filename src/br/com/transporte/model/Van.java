package br.com.transporte.model;

public class Van extends Veiculo {
    public static final double TARIFA_BASE = 8.00;
    public static final double TARIFA_POR_KM = 3.00;

    public Van(String placa, String modelo) {
        super(placa, modelo);
    }

    @Override
    public double calcularTarifa(double distancia) {
        return calcularComBase(distancia, TARIFA_BASE, TARIFA_POR_KM);
    }

    @Override
    public int getNivelCategoriaMaximo() { return 3; }
}
