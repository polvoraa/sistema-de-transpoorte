package br.com.transporte.app;

import br.com.transporte.service.SistemaTransporte;

public class Aplicacao {
    private Aplicacao() {
    }

    public static void main(String[] args) {
        new MenuConsole(new SistemaTransporte()).executar();
    }
}
