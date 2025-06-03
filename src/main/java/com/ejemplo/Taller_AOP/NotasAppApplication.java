package com.ejemplo.Taller_AOP;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class NotasAppApplication {
    public static void main(String[] args) {
        SpringApplication.run(NotasAppApplication.class, args);
    }
}