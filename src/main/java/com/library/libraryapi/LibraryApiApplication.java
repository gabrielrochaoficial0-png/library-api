package com.library.libraryapi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info = @Info(
        title = "API da Biblioteca",
        description = "API REST para gerenciamento de biblioteca (livros, membros e empréstimos)",
        version = "1.0.0"
))
public class LibraryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(LibraryApiApplication.class, args);
    }

}
