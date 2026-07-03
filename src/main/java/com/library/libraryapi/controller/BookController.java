package com.library.libraryapi.controller;

import com.library.libraryapi.dto.BookRequestDTO;
import com.library.libraryapi.dto.BookResponseDTO;
import com.library.libraryapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Books", description = "Gerenciamento do acervo de livros")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo livro")
    public ResponseEntity<BookResponseDTO> create(@Valid @RequestBody BookRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os livros")
    public ResponseEntity<List<BookResponseDTO>> findAll() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um livro pelo id")
    public ResponseEntity<BookResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um livro")
    public ResponseEntity<BookResponseDTO> update(@PathVariable Long id, @Valid @RequestBody BookRequestDTO dto) {
        return ResponseEntity.ok(bookService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um livro do acervo")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
