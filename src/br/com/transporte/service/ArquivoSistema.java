package br.com.transporte.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ArquivoSistema {
    private final Path caminho;

    public ArquivoSistema(Path caminho) {
        this.caminho = caminho.toAbsolutePath();
    }

    public SistemaTransporte carregar() throws IOException, ClassNotFoundException {
        if (Files.notExists(caminho)) {
            return new SistemaTransporte();
        }
        try (ObjectInputStream entrada = new ObjectInputStream(Files.newInputStream(caminho))) {
            Object dados = entrada.readObject();
            if (!(dados instanceof SistemaTransporte)) {
                throw new IOException("O arquivo não contém os dados do sistema.");
            }
            return (SistemaTransporte) dados;
        }
    }

    public void salvar(SistemaTransporte sistema) throws IOException {
        Path temporario = Files.createTempFile(caminho.getParent(), "transporte-", ".tmp");
        try {
            try (ObjectOutputStream saida = new ObjectOutputStream(Files.newOutputStream(temporario))) {
                saida.writeObject(sistema);
            }
            Files.move(temporario, caminho, StandardCopyOption.ATOMIC_MOVE,
                    StandardCopyOption.REPLACE_EXISTING);
        } finally {
            Files.deleteIfExists(temporario);
        }
    }
}
