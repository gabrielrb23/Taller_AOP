package com.ejemplo.Taller_AOP.excepciones;

public class NotaInvalidaException extends RuntimeException {
    public NotaInvalidaException(String mensaje) {
        super(mensaje);
    }
}
