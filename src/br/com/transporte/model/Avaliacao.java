package br.com.transporte.model;

import br.com.transporte.exception.NotaInvalidaException;

public class Avaliacao implements java.io.Serializable {
    private static final long serialVersionUID = 1L;
    private final int nota;
    private final String comentario;

    public Avaliacao(int nota, String comentario) {
        if (nota < 1 || nota > 5) {
            throw new NotaInvalidaException("A nota deve estar entre 1 e 5");
        }
        this.nota = nota;
        if (comentario == null) {
            this.comentario = "";
        } else {
            this.comentario = comentario.trim();
        }
    }

    public int getNota() {
        return nota;
    }

    public String getComentario() {
        return comentario;
    }

    @Override
    public String toString() {
        if (comentario.isEmpty()) {
            return nota + "/5";
        }
        return nota + "/5 - " + comentario;
    }
}
