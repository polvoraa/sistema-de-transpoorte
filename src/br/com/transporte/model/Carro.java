package br.com.transporte.model;

public class Carro extends Veiculo {
    private static final long serialVersionUID = 1L;
    public static final double TARIFA_BASE = 5.00;
    public static final double TARIFA_POR_KM = 2.00;

    public Carro(String placa, String modelo) {
        super(placa, modelo);
    }

    @Override
    public double calcularTarifa(double distancia) {
        return calcularComBase(distancia, TARIFA_BASE, TARIFA_POR_KM);
    }

    @Override
    public int getNivelCategoriaMaximo() { return 2; }
}
