package br.com.transporte.service;

import br.com.transporte.exception.CadastroDuplicadoException;
import br.com.transporte.exception.PassageiroNaoCadastradoException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.com.transporte.model.Corrida;
import br.com.transporte.model.EstadoCorrida;
import br.com.transporte.model.Motorista;
import br.com.transporte.model.Passageiro;
import br.com.transporte.model.Veiculo;
import br.com.transporte.strategy.CategoriaCorrida;
import br.com.transporte.strategy.FormaPagamento;

/** Mantém os cadastros da empresa; as regras de ciclo permanecem em Corrida. */
public class SistemaTransporte implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final List<Passageiro> passageiros = new ArrayList<>();
    private final List<Motorista> motoristas = new ArrayList<>();
    private final List<Veiculo> veiculos = new ArrayList<>();
    private final GerenciadorCorridas gerenciadorCorridas = new GerenciadorCorridas();

    public void cadastrarPassageiro(Passageiro passageiro) {
        if (passageiro == null) throw new IllegalArgumentException("O passageiro é obrigatório");
        if (buscarPassageiroPorCpf(passageiro.getCpf()) != null)
            throw new CadastroDuplicadoException("Já existe um passageiro com este CPF");
        passageiros.add(passageiro);
    }

    public void cadastrarVeiculo(Veiculo veiculo) {
        if (veiculo == null) throw new IllegalArgumentException("O veículo é obrigatório");
        if (buscarVeiculoPorPlaca(veiculo.getPlaca()) != null)
            throw new CadastroDuplicadoException("Já existe um veículo com esta placa");
        veiculos.add(veiculo);
    }

    public void cadastrarMotorista(Motorista motorista) {
        if (motorista == null) throw new IllegalArgumentException("O motorista é obrigatório");
        if (!veiculos.contains(motorista.getVeiculo()))
            throw new IllegalArgumentException("O veículo deve ser cadastrado antes do motorista");
        if (buscarMotoristaPorCpf(motorista.getCpf()) != null)
            throw new CadastroDuplicadoException("Já existe um motorista com este CPF");
        boolean cnhEmUso = false;
        boolean veiculoEmUso = false;

        for (Motorista motoristaCadastrado : motoristas) {
            if (motoristaCadastrado.getCnh().equalsIgnoreCase(motorista.getCnh())) {
                cnhEmUso = true;
            }
            if (motoristaCadastrado.getVeiculo() == motorista.getVeiculo()) {
                veiculoEmUso = true;
            }
        }

        if (cnhEmUso) {
            throw new CadastroDuplicadoException("CNH já cadastrada");
        }
        if (veiculoEmUso) {
            throw new CadastroDuplicadoException("Veículo já associado a outro motorista");
        }
        motoristas.add(motorista);
    }

    public Corrida solicitarCorrida(Passageiro passageiro, String origem, String destino,
            double distancia, FormaPagamento pagamento, CategoriaCorrida categoria) {
        if (!passageiros.contains(passageiro))
            throw new PassageiroNaoCadastradoException("O passageiro não está cadastrado");
        return passageiro.solicitarCorrida(gerenciadorCorridas, origem, destino, distancia,
                pagamento, categoria);
    }

    public Passageiro buscarPassageiroPorCpf(String cpf) {
        if (cpf == null) return null;
        for (Passageiro passageiro : passageiros) {
            if (passageiro.getCpf().equalsIgnoreCase(cpf.trim())) {
                return passageiro;
            }
        }
        return null;
    }

    public Motorista buscarMotoristaPorCpf(String cpf) {
        if (cpf == null) return null;
        for (Motorista motorista : motoristas) {
            if (motorista.getCpf().equalsIgnoreCase(cpf.trim())) {
                return motorista;
            }
        }
        return null;
    }

    public Veiculo buscarVeiculoPorPlaca(String placa) {
        if (placa == null) return null;
        for (Veiculo veiculo : veiculos) {
            if (veiculo.getPlaca().equalsIgnoreCase(placa.trim())) {
                return veiculo;
            }
        }
        return null;
    }

    public Corrida buscarCorridaPorId(int id) {
        for (Corrida corrida : gerenciadorCorridas.getCorridas()) {
            if (corrida.getId() == id) {
                return corrida;
            }
        }
        return null;
    }

    public List<Motorista> listarMotoristasDisponiveisPara(Corrida corrida) {
        List<Motorista> disponiveis = new ArrayList<>();
        if (corrida == null) {
            return disponiveis;
        }

        for (Motorista motorista : motoristas) {
            if (corrida.podeSerAceitaPor(motorista)) {
                disponiveis.add(motorista);
            }
        }
        return disponiveis;
    }

    public List<Corrida> listarCorridasPorEstado(EstadoCorrida estado) {
        return gerenciadorCorridas.listarPorEstado(estado);
    }

    public List<Veiculo> listarVeiculosSemMotorista() {
        List<Veiculo> livres = new ArrayList<>();

        for (Veiculo veiculo : veiculos) {
            boolean temMotorista = false;

            for (Motorista motorista : motoristas) {
                if (motorista.getVeiculo() == veiculo) {
                    temMotorista = true;
                }
            }

            if (!temMotorista) {
                livres.add(veiculo);
            }
        }
        return livres;
    }

    public List<Passageiro> getPassageiros() { return Collections.unmodifiableList(passageiros); }
    public List<Motorista> getMotoristas() { return Collections.unmodifiableList(motoristas); }
    public List<Veiculo> getVeiculos() { return Collections.unmodifiableList(veiculos); }
    public List<Corrida> getCorridas() { return gerenciadorCorridas.getCorridas(); }
}
