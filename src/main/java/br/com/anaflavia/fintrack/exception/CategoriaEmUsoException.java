package br.com.anaflavia.fintrack.exception;

public class CategoriaEmUsoException extends RuntimeException {

    public CategoriaEmUsoException(String mensagem) {
        super(mensagem);
    }
}