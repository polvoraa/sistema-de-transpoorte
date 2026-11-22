package br.com.transporte.app;

import java.io.IOException;
import java.nio.file.Path;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import br.com.transporte.service.ArquivoSistema;

public class Aplicacao {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ArquivoSistema arquivo = new ArquivoSistema(Path.of("transporte.dat"));
            try {
                new TelaTransporte(arquivo.carregar(), arquivo).setVisible(true);
            } catch (IOException | ClassNotFoundException erro) {
                JOptionPane.showMessageDialog(null, "Erro ao carregar os dados: " + erro.getMessage(),
                        "Sistema de Transporte", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
