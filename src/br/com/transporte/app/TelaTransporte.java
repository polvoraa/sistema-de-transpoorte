package br.com.transporte.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import br.com.transporte.exception.DistanciaInvalidaException;
import br.com.transporte.exception.EstadoCorridaInvalidoException;
import br.com.transporte.exception.MotoristaIndisponivelException;
import br.com.transporte.exception.NotaInvalidaException;
import br.com.transporte.exception.PassageiroNaoCadastradoException;
import br.com.transporte.exception.VeiculoIncompativelException;
import br.com.transporte.model.*;
import br.com.transporte.service.ArquivoSistema;
import br.com.transporte.service.SistemaTransporte;
import br.com.transporte.strategy.*;

public class TelaTransporte extends JFrame {
    private static final long serialVersionUID = 1L;
    private final SistemaTransporte sistema;
    private final ArquivoSistema arquivo;
    private final TabelaCorridas modelo;
    private final JTable tabela;
    private final JLabel mensagem = new JLabel("Selecione uma corrida na tabela.");
    private boolean precisaSalvar;

    public TelaTransporte(SistemaTransporte sistema, ArquivoSistema arquivo) {
        super("Sistema de Transporte");
        this.sistema = sistema;
        this.arquivo = arquivo;
        modelo = new TabelaCorridas(sistema.getCorridas());
        tabela = new JTable(modelo);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabela.setAutoCreateRowSorter(true);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        tabela.getTableHeader().setReorderingAllowed(false);
        for (int coluna = 0; coluna < tabela.getColumnCount(); coluna++) {
            tabela.getColumnModel().getColumn(coluna).setPreferredWidth(coluna == 0 ? 45 : 120);
        }

        JPanel acoes = new JPanel();
        botao(acoes, "Solicitar", this::solicitar);
        botao(acoes, "Aceitar", () -> aceitar(corridaSelecionada()));
        botao(acoes, "Iniciar", () -> executarNaCorrida(Corrida::iniciar));
        botao(acoes, "Finalizar", () -> executarNaCorrida(Corrida::finalizar));
        botao(acoes, "Cancelar", () -> executarNaCorrida(Corrida::cancelar));
        botao(acoes, "Avaliar", () -> avaliar(corridaSelecionada()));
        botao(acoes, "Salvar", () -> {
            if (salvar()) mensagem.setText("Dados salvos.");
        });

        tabela.getSelectionModel().addListSelectionListener(evento -> {
            if (!evento.getValueIsAdjusting() && tabela.getSelectedRow() >= 0) {
                mostrarCorrida(corridaSelecionada());
            }
        });
        add(acoes, BorderLayout.NORTH);
        add(new JScrollPane(tabela), BorderLayout.CENTER);
        add(mensagem, BorderLayout.SOUTH);
        setJMenuBar(criarMenu());
        setSize(1050, 460);
        setMinimumSize(new Dimension(650, 320));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evento) {
                if (!precisaSalvar || salvar()) dispose();
            }
        });
    }

    private JMenuBar criarMenu() {
        JMenuBar barra = new JMenuBar();
        JMenu cadastros = new JMenu("Cadastros");
        item(cadastros, "Passageiro", this::cadastrarPassageiro);
        item(cadastros, "Veículo", this::cadastrarVeiculo);
        item(cadastros, "Motorista", this::cadastrarMotorista);
        barra.add(cadastros);

        JMenu consultas = new JMenu("Consultas");
        item(consultas, "Passageiro", this::consultarPassageiro);
        item(consultas, "Motorista", this::consultarMotorista);
        barra.add(consultas);
        return barra;
    }

    private void botao(JPanel painel, String nome, Runnable acao) {
        JButton botao = new JButton(nome);
        botao.addActionListener(evento -> executar(acao));
        painel.add(botao);
    }

    private void item(JMenu menu, String nome, Runnable acao) {
        JMenuItem item = new JMenuItem(nome);
        item.addActionListener(evento -> executar(acao));
        menu.add(item);
    }

    private void executar(Runnable acao) {
        try {
            acao.run();
        } catch (IllegalArgumentException | IllegalStateException erro) {
            JOptionPane.showMessageDialog(this, erro.getMessage(), "Atenção", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void cadastrarPassageiro() {
        JTextField nome = new JTextField(18);
        JTextField cpf = new JTextField(18);
        JTextField telefone = new JTextField(18);
        JPanel painel = formulario();
        campo(painel, "Nome", nome);
        campo(painel, "CPF", cpf);
        campo(painel, "Telefone", telefone);
        if (confirmar("Cadastrar passageiro", painel)) {
            sistema.cadastrarPassageiro(new Passageiro(nome.getText(), cpf.getText(), telefone.getText()));
            registrarAlteracao(null, "Passageiro cadastrado.");
        }
    }

    private void cadastrarVeiculo() {
        JComboBox<String> tipo = new JComboBox<>();
        for (String nome : List.of("Moto", "Carro", "Van")) tipo.addItem(nome);
        JTextField placa = new JTextField(18);
        JTextField modelo = new JTextField(18);
        JPanel painel = formulario();
        campo(painel, "Tipo", tipo);
        campo(painel, "Placa", placa);
        campo(painel, "Modelo", modelo);
        if (confirmar("Cadastrar veículo", painel)) {
            Veiculo veiculo = switch (tipo.getSelectedIndex()) {
                case 0 -> new Moto(placa.getText(), modelo.getText());
                case 1 -> new Carro(placa.getText(), modelo.getText());
                default -> new Van(placa.getText(), modelo.getText());
            };
            sistema.cadastrarVeiculo(veiculo);
            registrarAlteracao(null, "Veículo cadastrado.");
        }
    }

    private void cadastrarMotorista() {
        List<Veiculo> disponiveis = sistema.listarVeiculosSemMotorista();
        if (disponiveis.isEmpty()) {
            throw new IllegalArgumentException("Cadastre um veículo livre primeiro.");
        }
        JComboBox<Veiculo> veiculo = new JComboBox<>();
        for (Veiculo livre : disponiveis) veiculo.addItem(livre);
        JTextField nome = new JTextField(18);
        JTextField cpf = new JTextField(18);
        JTextField telefone = new JTextField(18);
        JTextField cnh = new JTextField(18);
        JCheckBox disponivel = new JCheckBox("Disponível para corridas", true);
        JPanel painel = formulario();
        campo(painel, "Nome", nome);
        campo(painel, "CPF", cpf);
        campo(painel, "Telefone", telefone);
        campo(painel, "CNH", cnh);
        campo(painel, "Veículo", veiculo);
        campo(painel, "Situação", disponivel);
        if (confirmar("Cadastrar motorista", painel)) {
            Motorista motorista = new Motorista(nome.getText(), cpf.getText(), telefone.getText(),
                    cnh.getText(), veiculo.getItemAt(veiculo.getSelectedIndex()));
            if (disponivel.isSelected()) motorista.ficarDisponivel();
            sistema.cadastrarMotorista(motorista);
            registrarAlteracao(null, "Motorista cadastrado.");
        }
    }

    private void solicitar() {
        List<Passageiro> passageiros = sistema.getPassageiros();
        if (passageiros.isEmpty()) {
            throw new PassageiroNaoCadastradoException("Cadastre um passageiro primeiro.");
        }
        JPanel painel = formulario();
        JComboBox<Passageiro> resultados = pesquisa(painel, "Passageiro", passageiros);
        JTextField origem = new JTextField(18);
        JTextField destino = new JTextField(18);
        JTextField distancia = new JTextField(18);
        JComboBox<String> categoria = new JComboBox<>();
        for (String nome : List.of("Econômica", "Conforto", "Premium")) categoria.addItem(nome);
        JComboBox<String> pagamento = new JComboBox<>();
        for (String nome : List.of("Pix", "Cartão", "Dinheiro")) pagamento.addItem(nome);
        campo(painel, "Origem", origem);
        campo(painel, "Destino", destino);
        campo(painel, "Distância (km)", distancia);
        campo(painel, "Categoria", categoria);
        campo(painel, "Pagamento", pagamento);

        if (confirmar("Solicitar corrida", painel)) {
            Passageiro passageiro = (Passageiro) resultados.getSelectedItem();
            double km;
            try {
                km = Double.parseDouble(distancia.getText().trim().replace(',', '.'));
            } catch (NumberFormatException erro) {
                throw new DistanciaInvalidaException("Digite uma distância válida.");
            }
            List<CategoriaCorrida> categorias = List.of(new Economica(), new Conforto(), new Premium());
            List<FormaPagamento> pagamentos = List.of(new Pix(), new Cartao(), new Dinheiro());
            Corrida corrida = sistema.solicitarCorrida(passageiro, origem.getText(), destino.getText(), km,
                    pagamentos.get(pagamento.getSelectedIndex()), categorias.get(categoria.getSelectedIndex()));
            registrarAlteracao(corrida, "");
        }
    }

    private void aceitar(Corrida corrida) {
        if (corrida.getEstado() != EstadoCorrida.SOLICITADA) {
            throw new EstadoCorridaInvalidoException("Selecione uma corrida solicitada.");
        }
        if (sistema.getMotoristas().isEmpty()) {
            throw new MotoristaIndisponivelException("Cadastre um motorista primeiro.");
        }
        JPanel painel = formulario();
        JComboBox<Motorista> resultados = pesquisa(painel, "Motorista", sistema.getMotoristas());
        if (confirmar("Aceitar corrida #" + corrida.getId(), painel)) {
            corrida.aceitar((Motorista) resultados.getSelectedItem());
            registrarAlteracao(corrida, "");
        }
    }

    private void avaliar(Corrida corrida) {
        JPanel painel = formulario();
        JTextField nota = new JTextField(18);
        JTextField comentario = new JTextField(18);
        campo(painel, "Nota (1 a 5)", nota);
        campo(painel, "Comentário", comentario);
        if (confirmar("Avaliar corrida #" + corrida.getId(), painel)) {
            int numero;
            try {
                numero = Integer.parseInt(nota.getText().trim());
            } catch (NumberFormatException erro) {
                throw new NotaInvalidaException("Digite uma nota inteira de 1 a 5.");
            }
            corrida.avaliar(numero, comentario.getText());
            registrarAlteracao(corrida, "");
        }
    }

    private void consultarPassageiro() {
        JPanel painel = formulario();
        JComboBox<Passageiro> resultados = pesquisa(painel, "Passageiro", sistema.getPassageiros());
        if (confirmar("Consultar passageiro", painel)) {
            Passageiro pessoa = (Passageiro) resultados.getSelectedItem();
            if (pessoa == null) throw new PassageiroNaoCadastradoException("Selecione um passageiro.");
            StringBuilder resumo = new StringBuilder(pessoa.toString());
            resumo.append("\nCorridas solicitadas: ").append(pessoa.getCorridasSolicitadas().size());
            for (Corrida corrida : pessoa.getCorridasSolicitadas()) resumo.append("\n").append(corrida);
            mostrarConsulta("Passageiro", resumo.toString());
        }
    }

    private void consultarMotorista() {
        JPanel painel = formulario();
        JComboBox<Motorista> resultados = pesquisa(painel, "Motorista", sistema.getMotoristas());
        if (confirmar("Consultar motorista", painel)) {
            Motorista pessoa = (Motorista) resultados.getSelectedItem();
            if (pessoa == null) throw new MotoristaIndisponivelException("Selecione um motorista.");
            StringBuilder resumo = new StringBuilder(pessoa.toString());
            resumo.append("\nTelefone: ").append(pessoa.getTelefone());
            resumo.append(String.format("%nMédia das avaliações: %.2f (%d avaliações)",
                    pessoa.calcularMediaAvaliacoes(), pessoa.getAvaliacoes().size()));
            for (Avaliacao avaliacao : pessoa.getAvaliacoes()) resumo.append("\n").append(avaliacao);
            mostrarConsulta("Motorista", resumo.toString());
        }
    }

    private void mostrarConsulta(String titulo, String resumo) {
        JTextArea texto = new JTextArea(resumo, 8, 55);
        texto.setEditable(false);
        texto.setCaretPosition(0);
        JOptionPane.showMessageDialog(this, new JScrollPane(texto), titulo, JOptionPane.INFORMATION_MESSAGE);
    }

    private <T extends Pessoa> JComboBox<T> pesquisa(JPanel painel, String rotulo, List<T> pessoas) {
        JTextField nome = new JTextField(18);
        JComboBox<T> resultados = new JComboBox<>();
        campo(painel, "Pesquisar por nome", nome);
        campo(painel, rotulo, resultados);
        Runnable filtrar = () -> {
            String parte = nome.getText().trim().toLowerCase(Locale.ROOT);
            resultados.removeAllItems();
            for (T pessoa : pessoas) {
                if (pessoa.getNome().toLowerCase(Locale.ROOT).contains(parte)) {
                    resultados.addItem(pessoa);
                }
            }
        };
        nome.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent evento) { filtrar.run(); }
            public void removeUpdate(DocumentEvent evento) { filtrar.run(); }
            public void changedUpdate(DocumentEvent evento) { filtrar.run(); }
        });
        filtrar.run();
        return resultados;
    }

    private JPanel formulario() {
        return new JPanel(new GridLayout(0, 2, 8, 6));
    }

    private void campo(JPanel painel, String nome, JComponent componente) {
        JLabel rotulo = new JLabel(nome);
        rotulo.setLabelFor(componente);
        painel.add(rotulo);
        painel.add(componente);
    }

    private boolean confirmar(String titulo, JPanel painel) {
        return JOptionPane.showConfirmDialog(this, painel, titulo, JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }

    private Corrida corridaSelecionada() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) throw new EstadoCorridaInvalidoException("Selecione uma corrida na tabela.");
        return modelo.getCorrida(tabela.convertRowIndexToModel(linha));
    }

    private void executarNaCorrida(Consumer<Corrida> acao) {
        Corrida corrida = corridaSelecionada();
        acao.accept(corrida);
        registrarAlteracao(corrida, "");
    }

    private void mostrarCorrida(Corrida corrida) {
        String custo = TabelaCorridas.valorFinal(corrida);
        String avaliacao = corrida.getAvaliacao() == null ? "Sem avaliação" : corrida.getAvaliacao().toString();
        mensagem.setText("Corrida #" + corrida.getId() + " | " + corrida.getEstado()
                + " | " + custo + " | " + avaliacao);
    }

    private void registrarAlteracao(Corrida corrida, String aviso) {
        precisaSalvar = true;
        if (corrida == null) {
            mensagem.setText(aviso);
        } else {
            modelo.fireTableDataChanged();
            int linha = tabela.convertRowIndexToView(sistema.getCorridas().indexOf(corrida));
            tabela.setRowSelectionInterval(linha, linha);
            tabela.scrollRectToVisible(tabela.getCellRect(linha, 0, true));
            mostrarCorrida(corrida);
        }
        salvar();
    }

    private boolean salvar() {
        try {
            arquivo.salvar(sistema);
            precisaSalvar = false;
            return true;
        } catch (IOException erro) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar os dados. Use Salvar para tentar de novo.\n"
                    + erro.getMessage(), "Erro de gravação", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
}
