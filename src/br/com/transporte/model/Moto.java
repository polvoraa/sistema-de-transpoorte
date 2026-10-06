package br.com.transporte.model;

public class Moto extends Veiculo {
    public static final double TARIFA_BASE = 3.00;
    public static final double TARIFA_POR_KM = 1.50;

    public Moto(String placa, String modelo) {
        super(placa, modelo);
    }

    @Override
    public double calcularTarifa(double distancia) {
        return calcularComBase(distancia, TARIFA_BASE, TARIFA_POR_KM);
    }

    @Override
    public int getNivelCategoriaMaximo() { return 1; }
}
