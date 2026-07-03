package com.library.libraryapi.controller;

import com.library.libraryapi.dto.LoanRequestDTO;
import com.library.libraryapi.dto.LoanResponseDTO;
import com.library.libraryapi.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Gerenciamento de empréstimos de livros")
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    @Operation(summary = "Registrar um novo empréstimo")
    public ResponseEntity<LoanResponseDTO> borrow(@Valid @RequestBody LoanRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(loanService.borrow(dto));
    }

    @PatchMapping("/{id}/return")
    @Operation(summary = "Registrar a devolução de um livro")
    public ResponseEntity<LoanResponseDTO> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.returnBook(id));
    }

    @GetMapping
    @Operation(summary = "Listar todos os empréstimos")
    public ResponseEntity<List<LoanResponseDTO>> findAll() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um empréstimo pelo id")
    public ResponseEntity<LoanResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(loanService.findById(id));
    }

    @GetMapping("/member/{memberId}")
    @Operation(summary = "Listar empréstimos de um membro específico")
    public ResponseEntity<List<LoanResponseDTO>> findByMember(@PathVariable Long memberId) {
        return ResponseEntity.ok(loanService.findByMember(memberId));
    }
}
