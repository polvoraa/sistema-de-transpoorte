package br.com.transporte.model;

import br.com.transporte.exception.DistanciaInvalidaException;
import br.com.transporte.exception.MotoristaIndisponivelException;
import br.com.transporte.exception.VeiculoIncompativelException;
import br.com.transporte.exception.EstadoCorridaInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.com.transporte.strategy.CategoriaCorrida;
import br.com.transporte.strategy.FormaPagamento;

public class Corrida implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final int id;
    private final Passageiro passageiro;
    private final String origem;
    private final String destino;
    private final double distancia;
    private final FormaPagamento formaPagamento;
    private final CategoriaCorrida categoria;
    private EstadoCorrida estado = EstadoCorrida.SOLICITADA;
    private Motorista motorista;
    private Veiculo veiculo;
    private Avaliacao avaliacao;
    private double valor;
    private double valorFinal;

    public Corrida(int id, Passageiro passageiro, String origem, String destino,
            double distancia, FormaPagamento formaPagamento, CategoriaCorrida categoria) {
        if (id <= 0) {
            throw new IllegalArgumentException("O id deve ser positivo");
        }
        if (passageiro == null || formaPagamento == null || categoria == null) {
            throw new IllegalArgumentException("Passageiro, pagamento e categoria são obrigatórios");
        }
        if (!Double.isFinite(distancia) || distancia <= 0) {
            throw new DistanciaInvalidaException("A distância deve ser maior que zero");
        }
        this.id = id;
        this.passageiro = passageiro;
        this.origem = Pessoa.validarTexto(origem, "origem");
        this.destino = Pessoa.validarTexto(destino, "destino");
        this.distancia = distancia;
        this.formaPagamento = formaPagamento;
        this.categoria = categoria;
    }

    public void aceitar(Motorista motorista) {
        exigirEstado(EstadoCorrida.SOLICITADA, "aceitar");
        if (motorista == null || !motorista.isDisponivel()) {
            throw new MotoristaIndisponivelException("O motorista deve estar disponível");
        }
        if (!categoria.isCompativel(motorista.getVeiculo())) {
            throw new VeiculoIncompativelException("O veículo do motorista não é compatível com a categoria "
                    + categoria.getNome());
        }
        Veiculo veiculoEscolhido = motorista.getVeiculo();
        atualizarValores(veiculoEscolhido);
        this.motorista = motorista;
        this.veiculo = veiculoEscolhido;
        motorista.ficarIndisponivel();
        estado = EstadoCorrida.ACEITA;
    }

    public void iniciar() {
        exigirEstado(EstadoCorrida.ACEITA, "iniciar");
        estado = EstadoCorrida.EM_ANDAMENTO;
    }

    public void finalizar() {
        exigirEstado(EstadoCorrida.EM_ANDAMENTO, "finalizar");
        calcularValor();
        estado = EstadoCorrida.FINALIZADA;
        motorista.ficarDisponivel();
    }

    public void cancelar() {
        if (estado != EstadoCorrida.SOLICITADA && estado != EstadoCorrida.ACEITA) {
            throw new EstadoCorridaInvalidoException("Só é possível cancelar uma corrida solicitada ou aceita");
        }
        if (motorista != null) {
            motorista.ficarDisponivel();
        }
        estado = EstadoCorrida.CANCELADA;
    }

    public double calcularValor() {
        if (veiculo == null) {
            throw new EstadoCorridaInvalidoException("A corrida precisa ser aceita antes do cálculo");
        }
        atualizarValores(veiculo);
        return valorFinal;
    }

    private void atualizarValores(Veiculo veiculo) {
        double tarifa = arredondar(veiculo.calcularTarifa(distancia));
        double total = arredondar(formaPagamento.aplicar(categoria.aplicarAcrescimo(tarifa)));
        valor = tarifa;
        valorFinal = total;
    }

    public void avaliar(int nota, String comentario) {
        if (estado != EstadoCorrida.FINALIZADA) {
            throw new EstadoCorridaInvalidoException("Apenas corridas finalizadas podem ser avaliadas");
        }
        if (avaliacao != null) {
            throw new EstadoCorridaInvalidoException("A corrida já foi avaliada");
        }
        avaliacao = new Avaliacao(nota, comentario);
        motorista.adicionarAvaliacao(avaliacao);
    }

    private void exigirEstado(EstadoCorrida esperado, String acao) {
        if (estado != esperado) {
            throw new EstadoCorridaInvalidoException("Não é possível " + acao + " uma corrida no estado " + estado);
        }
    }

    private static double arredondar(double numero) {
        if (!Double.isFinite(numero)) {
            throw new DistanciaInvalidaException("O valor da corrida excedeu o limite de cálculo");
        }
        return BigDecimal.valueOf(numero).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    public int getId() { return id; }
    public Passageiro getPassageiro() { return passageiro; }
    public String getOrigem() { return origem; }
    public String getDestino() { return destino; }
    public double getDistancia() { return distancia; }
    public EstadoCorrida getEstado() { return estado; }
    public Motorista getMotorista() { return motorista; }
    public Veiculo getVeiculo() { return veiculo; }
    public FormaPagamento getFormaPagamento() { return formaPagamento; }
    public CategoriaCorrida getCategoria() { return categoria; }
    public Avaliacao getAvaliacao() { return avaliacao; }
    public double getValor() { return valor; }
    public double getValorFinal() { return valorFinal; }

    public boolean podeSerAceitaPor(Motorista candidato) {
        if (estado != EstadoCorrida.SOLICITADA) {
            return false;
        }
        if (candidato == null) {
            return false;
        }
        if (!candidato.isDisponivel()) {
            return false;
        }
        return categoria.isCompativel(candidato.getVeiculo());
    }

    @Override
    public String toString() {
        return String.format("Corrida #%d | %s -> %s | %.1f km | %s | R$ %.2f",
                id, origem, destino, distancia, estado, valorFinal);
    }
}
