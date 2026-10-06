package br.com.transporte.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import br.com.transporte.app.MenuConsole;
import br.com.transporte.model.Carro;
import br.com.transporte.model.Corrida;
import br.com.transporte.model.EstadoCorrida;
import br.com.transporte.model.Motorista;
import br.com.transporte.model.Passageiro;
import br.com.transporte.model.Van;
import br.com.transporte.service.SistemaTransporte;
import br.com.transporte.strategy.Conforto;
import br.com.transporte.strategy.Pix;
import br.com.transporte.strategy.Premium;

public class TesteSistema {
    private TesteSistema() {
    }

    public static void main(String[] args) {
        SistemaTransporte sistema = new SistemaTransporte();
        Passageiro passageiro = new Passageiro("Passageiro", "111", "9999");
        Carro carro = new Carro("ABC1D23", "Sedã");
        Motorista motorista = new Motorista("Motorista", "222", "8888", "CNH-1",
                carro);
        sistema.cadastrarPassageiro(passageiro);
        sistema.cadastrarVeiculo(carro);
        sistema.cadastrarMotorista(motorista);
        motorista.ficarDisponivel();

        Corrida corrida = sistema.solicitarCorrida(passageiro, "Origem", "Destino",
                10, new Pix(), new Conforto());
        assert corrida.getEstado() == EstadoCorrida.SOLICITADA;
        assert sistema.listarCorridasPorEstado(EstadoCorrida.SOLICITADA).size() == 1;

        corrida.aceitar(motorista);
        assert !motorista.isDisponivel();
        assert Math.abs(corrida.getValorFinal() - 28.50) < 0.001;

        corrida.iniciar();
        corrida.finalizar();
        assert motorista.isDisponivel();
        assert corrida.getEstado() == EstadoCorrida.FINALIZADA;

        corrida.avaliar(5, "Excelente");
        assert motorista.calcularMediaAvaliacoes() == 5.0;
        assert passageiro.getCorridasSolicitadas().size() == 1;

        boolean bloqueouTransicaoInvalida = false;
        try {
            corrida.cancelar();
        } catch (IllegalStateException esperado) {
            bloqueouTransicaoInvalida = true;
        }
        assert bloqueouTransicaoInvalida;

        Van van = new Van("VAN1A11", "Van executiva");
        Motorista motoristaPremium = new Motorista("Motorista Premium", "333", "7777",
                "CNH-2", van);
        sistema.cadastrarVeiculo(van);
        sistema.cadastrarMotorista(motoristaPremium);
        motoristaPremium.ficarDisponivel();
        Corrida premium = sistema.solicitarCorrida(passageiro, "A", "B", 5,
                new Pix(), new Premium());
        assert !premium.podeSerAceitaPor(motorista);
        assert premium.podeSerAceitaPor(motoristaPremium);
        premium.aceitar(motoristaPremium);
        assert premium.getVeiculo() == van;

        testarFluxoDoMenu();
        System.out.println("Todos os testes passaram.");
    }

    private static void testarFluxoDoMenu() {
        String comandos = String.join("\n",
                "1", "3", "2", "TST1A23", "Carro de teste",
                "1", "Ana", "CPF-P", "9999",
                "2", "TST1A23", "Carlos", "CPF-M", "8888", "CNH-T", "S", "0",
                "2", "1", "CPF-P", "Origem", "Destino", "10", "2", "1",
                "2", "1", "CPF-M", "3", "1", "4", "1", "6", "1", "5", "Ótima viagem", "0",
                "3", "1", "2", "CPF-P", "3", "CPF-M", "0", "0") + "\n";
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        MenuConsole menu = new MenuConsole(new SistemaTransporte(),
                new ByteArrayInputStream(comandos.getBytes(StandardCharsets.UTF_8)),
                new PrintStream(bytes, true, StandardCharsets.UTF_8));
        menu.executar();
        String resultado = bytes.toString(StandardCharsets.UTF_8);
        assert resultado.contains("Corrida finalizada");
        assert resultado.contains("Avaliação registrada");
        assert resultado.contains("Média das avaliações: 5,00")
                || resultado.contains("Média das avaliações: 5.00");
    }
}
