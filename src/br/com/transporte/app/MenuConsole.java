package br.com.transporte.app;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import br.com.transporte.model.Carro;
import br.com.transporte.model.Corrida;
import br.com.transporte.model.EstadoCorrida;
import br.com.transporte.model.Moto;
import br.com.transporte.model.Motorista;
import br.com.transporte.model.Passageiro;
import br.com.transporte.model.Van;
import br.com.transporte.model.Veiculo;
import br.com.transporte.service.SistemaTransporte;
import br.com.transporte.strategy.Cartao;
import br.com.transporte.strategy.CategoriaCorrida;
import br.com.transporte.strategy.Conforto;
import br.com.transporte.strategy.Dinheiro;
import br.com.transporte.strategy.Economica;
import br.com.transporte.strategy.FormaPagamento;
import br.com.transporte.strategy.Pix;
import br.com.transporte.strategy.Premium;

/** Única interface de console do sistema. Não contém regras do domínio. */
public class MenuConsole {
    private final SistemaTransporte sistema;
    private final Scanner entrada;
    private final PrintStream saida;

    public MenuConsole(SistemaTransporte sistema) {
        this(sistema, System.in, System.out);
    }

    public MenuConsole(SistemaTransporte sistema, InputStream entrada, PrintStream saida) {
        if (sistema == null || entrada == null || saida == null)
            throw new IllegalArgumentException("Sistema, entrada e saída são obrigatórios");
        this.sistema = sistema;
        this.entrada = new Scanner(entrada);
        this.saida = saida;
    }

    public void executar() {
        int opcao;
        do {
            titulo("SISTEMA DE TRANSPORTE");
            saida.println("1 - CADASTROS");
            saida.println("2 - CORRIDAS");
            saida.println("3 - CONSULTAS");
            saida.println("0 - Sair");
            opcao = lerInteiro("Opção: ");
            if (opcao == 1) {
                menuCadastros();
            } else if (opcao == 2) {
                menuCorridas();
            } else if (opcao == 3) {
                menuConsultas();
            } else if (opcao == 0) {
                saida.println("Sistema encerrado.");
            } else {
                saida.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void menuCadastros() {
        int opcao;
        do {
            titulo("SISTEMA DE TRANSPORTE - Cadastros");
            saida.println("1 - Cadastrar passageiro");
            saida.println("2 - Cadastrar motorista");
            saida.println("3 - Cadastrar veículo");
            saida.println("0 - Voltar");
            opcao = lerInteiro("Opção: ");
            if (opcao == 1) {
                executarComTratamento(this::cadastrarPassageiro);
            } else if (opcao == 2) {
                executarComTratamento(this::cadastrarMotorista);
            } else if (opcao == 3) {
                executarComTratamento(this::cadastrarVeiculo);
            } else if (opcao != 0) {
                saida.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void menuCorridas() {
        int opcao;
        do {
            titulo("SISTEMA DE TRANSPORTE - Corridas");
            saida.println("1 - Solicitar corrida");
            saida.println("2 - Aceitar corrida");
            saida.println("3 - Iniciar corrida");
            saida.println("4 - Finalizar corrida");
            saida.println("5 - Cancelar corrida");
            saida.println("6 - Avaliar motorista");
            saida.println("0 - Voltar");
            opcao = lerInteiro("Opção: ");
            if (opcao == 1) {
                executarComTratamento(this::solicitarCorrida);
            } else if (opcao == 2) {
                executarComTratamento(this::aceitarCorrida);
            } else if (opcao == 3) {
                executarComTratamento(this::iniciarCorrida);
            } else if (opcao == 4) {
                executarComTratamento(this::finalizarCorrida);
            } else if (opcao == 5) {
                executarComTratamento(this::cancelarCorrida);
            } else if (opcao == 6) {
                executarComTratamento(this::avaliarMotorista);
            } else if (opcao != 0) {
                saida.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void menuConsultas() {
        int opcao;
        do {
            titulo("SISTEMA DE TRANSPORTE - Consultas");
            saida.println("1 - Listar corridas");
            saida.println("2 - Consultar passageiro");
            saida.println("3 - Consultar motorista");
            saida.println("0 - Voltar");
            opcao = lerInteiro("Opção: ");
            if (opcao == 1) {
                listarCorridas(sistema.getCorridas());
            } else if (opcao == 2) {
                consultarPassageiro();
            } else if (opcao == 3) {
                consultarMotorista();
            } else if (opcao != 0) {
                saida.println("Opção inválida.");
            }
        } while (opcao != 0);
    }

    private void cadastrarPassageiro() {
        Passageiro passageiro = new Passageiro(lerTexto("Nome: "), lerTexto("CPF: "),
                lerTexto("Telefone: "));
        sistema.cadastrarPassageiro(passageiro);
        saida.println("Passageiro cadastrado com sucesso.");
    }

    private void cadastrarVeiculo() {
        saida.println("1 - Moto | 2 - Carro | 3 - Van");
        int tipo = lerInteiro("Tipo: ");
        String placa = lerTexto("Placa: ");
        String modelo = lerTexto("Modelo: ");
        Veiculo veiculo;
        if (tipo == 1) {
            veiculo = new Moto(placa, modelo);
        } else if (tipo == 2) {
            veiculo = new Carro(placa, modelo);
        } else if (tipo == 3) {
            veiculo = new Van(placa, modelo);
        } else {
            throw new IllegalArgumentException("Tipo de veículo inválido");
        }
        sistema.cadastrarVeiculo(veiculo);
        saida.println("Veículo cadastrado com sucesso.");
    }

    private void cadastrarMotorista() {
        List<Veiculo> livres = sistema.listarVeiculosSemMotorista();
        if (livres.isEmpty()) {
            saida.println("Cadastre primeiro um veículo que ainda não tenha motorista.");
            return;
        }
        saida.println("Veículos disponíveis:");
        for (Veiculo veiculoLivre : livres) {
            saida.println("- " + veiculoLivre);
        }
        Veiculo veiculo = sistema.buscarVeiculoPorPlaca(lerTexto("Placa do veículo: "));
        if (!livres.contains(veiculo)) throw new IllegalArgumentException("Veículo inexistente ou já associado");

        Motorista motorista = new Motorista(lerTexto("Nome: "), lerTexto("CPF: "),
                lerTexto("Telefone: "), lerTexto("CNH: "), veiculo);
        if (lerSimNao("Motorista disponível? (S/N): ")) {
            motorista.ficarDisponivel();
        }
        sistema.cadastrarMotorista(motorista);
        saida.println("Motorista cadastrado com sucesso.");
    }

    private void solicitarCorrida() {
        if (sistema.getPassageiros().isEmpty()) {
            saida.println("Não há passageiros cadastrados.");
            return;
        }
        for (Passageiro passageiroCadastrado : sistema.getPassageiros()) {
            saida.println("- " + passageiroCadastrado);
        }
        Passageiro passageiro = sistema.buscarPassageiroPorCpf(lerTexto("CPF do passageiro: "));
        if (passageiro == null) throw new IllegalArgumentException("Passageiro não encontrado");

        String origem = lerTexto("Origem: ");
        String destino = lerTexto("Destino: ");
        double distancia = lerDouble("Distância estimada (km): ");
        CategoriaCorrida categoria = escolherCategoria();
        FormaPagamento pagamento = escolherPagamento();
        Corrida corrida = sistema.solicitarCorrida(passageiro, origem, destino, distancia,
                pagamento, categoria);
        saida.println("Corrida solicitada. Identificador: " + corrida.getId());
    }

    private void aceitarCorrida() {
        Corrida corrida = escolherCorrida(sistema.listarCorridasPorEstado(EstadoCorrida.SOLICITADA));
        if (corrida == null) return;
        List<Motorista> disponiveis = sistema.listarMotoristasDisponiveisPara(corrida);
        if (disponiveis.isEmpty()) {
            saida.println("Não há motoristas disponíveis com veículo compatível.");
            return;
        }
        saida.println("Motoristas compatíveis:");
        for (Motorista motoristaDisponivel : disponiveis) {
            saida.println("- " + motoristaDisponivel);
        }
        Motorista motorista = sistema.buscarMotoristaPorCpf(lerTexto("CPF do motorista: "));
        if (!disponiveis.contains(motorista))
            throw new IllegalArgumentException("Motorista inexistente, indisponível ou incompatível");
        corrida.aceitar(motorista);
        saida.printf("Corrida aceita. Valor previsto: R$ %.2f%n", corrida.getValorFinal());
    }

    private void iniciarCorrida() {
        Corrida corrida = escolherCorrida(sistema.listarCorridasPorEstado(EstadoCorrida.ACEITA));
        if (corrida == null) return;
        corrida.iniciar();
        saida.println("Corrida iniciada.");
    }

    private void finalizarCorrida() {
        Corrida corrida = escolherCorrida(sistema.listarCorridasPorEstado(EstadoCorrida.EM_ANDAMENTO));
        if (corrida == null) return;
        corrida.finalizar();
        saida.printf("Corrida finalizada. Valor da tarifa: R$ %.2f | Valor final: R$ %.2f%n",
                corrida.getValor(), corrida.getValorFinal());
    }

    private void cancelarCorrida() {
        List<Corrida> cancelaveis = new ArrayList<>();
        for (Corrida corrida : sistema.getCorridas()) {
            if (corrida.getEstado() == EstadoCorrida.SOLICITADA
                    || corrida.getEstado() == EstadoCorrida.ACEITA) {
                cancelaveis.add(corrida);
            }
        }
        Corrida corrida = escolherCorrida(cancelaveis);
        if (corrida == null) return;
        corrida.cancelar();
        saida.println("Corrida cancelada.");
    }

    private void avaliarMotorista() {
        List<Corrida> avaliaveis = new ArrayList<>();
        for (Corrida corrida : sistema.listarCorridasPorEstado(EstadoCorrida.FINALIZADA)) {
            if (corrida.getAvaliacao() == null) {
                avaliaveis.add(corrida);
            }
        }
        Corrida corrida = escolherCorrida(avaliaveis);
        if (corrida == null) return;
        int nota = lerInteiro("Nota (1 a 5): ");
        saida.print("Comentário (opcional): ");
        String comentario = lerLinha();
        corrida.avaliar(nota, comentario);
        saida.println("Avaliação registrada.");
    }

    private void consultarPassageiro() {
        Passageiro passageiro = sistema.buscarPassageiroPorCpf(lerTexto("CPF: "));
        if (passageiro == null) {
            saida.println("Passageiro não encontrado.");
            return;
        }
        saida.println(passageiro);
        saida.println("Corridas solicitadas: " + passageiro.getCorridasSolicitadas().size());
        listarCorridas(passageiro.getCorridasSolicitadas());
    }

    private void consultarMotorista() {
        Motorista motorista = sistema.buscarMotoristaPorCpf(lerTexto("CPF: "));
        if (motorista == null) {
            saida.println("Motorista não encontrado.");
            return;
        }
        saida.println(motorista);
        saida.printf("Média das avaliações: %.2f (%d avaliação(ões))%n",
                motorista.calcularMediaAvaliacoes(), motorista.getAvaliacoes().size());
        for (br.com.transporte.model.Avaliacao avaliacao : motorista.getAvaliacoes()) {
            saida.println("- " + avaliacao);
        }
    }

    private Corrida escolherCorrida(List<Corrida> corridas) {
        if (corridas.isEmpty()) {
            saida.println("Não há corridas disponíveis para esta operação.");
            return null;
        }
        listarCorridas(corridas);
        Corrida corrida = sistema.buscarCorridaPorId(lerInteiro("Número da corrida: "));
        if (!corridas.contains(corrida)) throw new IllegalArgumentException("Corrida inválida para esta operação");
        return corrida;
    }

    private void listarCorridas(List<Corrida> corridas) {
        if (corridas.isEmpty()) {
            saida.println("Nenhuma corrida encontrada.");
            return;
        }
        for (Corrida c : corridas) {
            String motorista;
            if (c.getMotorista() == null) {
                motorista = "não definido";
            } else {
                motorista = c.getMotorista().getNome();
            }

            String veiculo;
            if (c.getVeiculo() == null) {
                veiculo = "não definido";
            } else {
                veiculo = c.getVeiculo().toString();
            }
            saida.printf("#%d | Passageiro: %s | Motorista: %s | Veículo: %s%n",
                    c.getId(), c.getPassageiro().getNome(), motorista, veiculo);
            saida.printf("   %s -> %s | %.1f km | %s | %s | %s | R$ %.2f%n",
                    c.getOrigem(), c.getDestino(), c.getDistancia(), c.getCategoria().getNome(),
                    c.getFormaPagamento().getNome(), c.getEstado(), c.getValorFinal());
        }
    }

    private CategoriaCorrida escolherCategoria() {
        saida.println("1 - Econômica (qualquer veículo) | 2 - Conforto (Carro/Van) | 3 - Premium (Van)");
        int opcao = lerInteiro("Categoria: ");
        if (opcao == 1) {
            return new Economica();
        } else if (opcao == 2) {
            return new Conforto();
        } else if (opcao == 3) {
            return new Premium();
        }
        throw new IllegalArgumentException("Categoria inválida");
    }

    private FormaPagamento escolherPagamento() {
        saida.println("1 - Pix | 2 - Cartão | 3 - Dinheiro");
        int opcao = lerInteiro("Forma de pagamento: ");
        if (opcao == 1) {
            return new Pix();
        } else if (opcao == 2) {
            return new Cartao();
        } else if (opcao == 3) {
            return new Dinheiro();
        }
        throw new IllegalArgumentException("Forma de pagamento inválida");
    }

    private void titulo(String texto) {
        saida.println("\n=========================================");
        saida.println(" " + texto);
        saida.println("=========================================");
    }

    private void executarComTratamento(Runnable operacao) {
        try {
            operacao.run();
        } catch (IllegalArgumentException | IllegalStateException erro) {
            saida.println("Não foi possível concluir: " + erro.getMessage());
        }
    }

    private String lerTexto(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String texto = lerLinha().trim();
            if (!texto.isEmpty()) {
                return texto;
            }
            saida.println("O valor não pode ficar vazio.");
        }
    }

    private int lerInteiro(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String texto = lerLinha().trim();
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException erro) {
                saida.println("Digite um número inteiro válido.");
            }
        }
    }

    private double lerDouble(String mensagem) {
        while (true) {
            saida.print(mensagem);
            String texto = lerLinha().trim().replace(',', '.');
            try {
                return Double.parseDouble(texto);
            } catch (NumberFormatException erro) {
                saida.println("Digite um número válido.");
            }
        }
    }

    private boolean lerSimNao(String mensagem) {
        while (true) {
            String resposta = lerTexto(mensagem);
            if (resposta.equalsIgnoreCase("S")) {
                return true;
            }
            if (resposta.equalsIgnoreCase("N")) {
                return false;
            }
            saida.println("Responda S ou N.");
        }
    }

    private String lerLinha() {
        if (entrada.hasNextLine()) {
            return entrada.nextLine();
        }
        return "0";
    }
}
