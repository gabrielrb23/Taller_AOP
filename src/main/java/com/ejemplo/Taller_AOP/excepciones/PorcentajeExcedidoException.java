package com.ejemplo.Taller_AOP.excepciones;

public class PorcentajeExcedidoException extends RuntimeException {
    public PorcentajeExcedidoException(String mensaje) {
        super(mensaje);
    }
}