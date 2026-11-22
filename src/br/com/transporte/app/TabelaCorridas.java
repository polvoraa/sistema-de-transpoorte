package br.com.transporte.app;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import javax.swing.table.AbstractTableModel;

import br.com.transporte.model.Corrida;

class TabelaCorridas extends AbstractTableModel {
    private static final long serialVersionUID = 1L;
    private final List<Corrida> corridas;
    private final List<String> colunas = List.of("Nº", "Passageiro", "Origem", "Destino",
            "Distância (km)", "Categoria", "Pagamento", "Estado", "Motorista", "Veículo",
            "Tarifa", "Custo final", "Avaliação");

    TabelaCorridas(List<Corrida> corridas) {
        this.corridas = corridas;
    }

    Corrida getCorrida(int linha) {
        return corridas.get(linha);
    }

    @Override
    public int getRowCount() {
        return corridas.size();
    }

    @Override
    public int getColumnCount() {
        return colunas.size();
    }

    @Override
    public String getColumnName(int coluna) {
        return colunas.get(coluna);
    }

    @Override
    public Class<?> getColumnClass(int coluna) {
        if (coluna == 0) return Integer.class;
        if (coluna == 4) return Double.class;
        return String.class;
    }

    @Override
    public Object getValueAt(int linha, int coluna) {
        Corrida c = getCorrida(linha);
        return switch (coluna) {
            case 0 -> c.getId();
            case 1 -> c.getPassageiro().getNome();
            case 2 -> c.getOrigem();
            case 3 -> c.getDestino();
            case 4 -> c.getDistancia();
            case 5 -> c.getCategoria().getNome();
            case 6 -> c.getFormaPagamento().getNome();
            case 7 -> c.getEstado().name();
            case 8 -> c.getMotorista() == null ? "-" : c.getMotorista().getNome();
            case 9 -> c.getVeiculo() == null ? "-" : c.getVeiculo().toString();
            case 10 -> c.getVeiculo() == null ? "-" : moeda(c.getValor());
            case 11 -> valorFinal(c);
            case 12 -> c.getAvaliacao() == null ? "-" : c.getAvaliacao().toString();
            default -> throw new IndexOutOfBoundsException(coluna);
        };
    }

    static String valorFinal(Corrida corrida) {
        return corrida.getVeiculo() == null ? "Sem custo calculado" : moeda(corrida.getValorFinal());
    }

    private static String moeda(double valor) {
        return NumberFormat.getCurrencyInstance(Locale.forLanguageTag("pt-BR")).format(valor);
    }
}
