package com.library.libraryapi.controller;

import com.library.libraryapi.dto.MemberRequestDTO;
import com.library.libraryapi.dto.MemberResponseDTO;
import com.library.libraryapi.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "Members", description = "Gerenciamento dos membros da biblioteca")
public class MemberController {

    private final MemberService memberService;

    @PostMapping
    @Operation(summary = "Cadastrar um novo membro")
    public ResponseEntity<MemberResponseDTO> create(@Valid @RequestBody MemberRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(memberService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Listar todos os membros")
    public ResponseEntity<List<MemberResponseDTO>> findAll() {
        return ResponseEntity.ok(memberService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar um membro pelo id")
    public ResponseEntity<MemberResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(memberService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar os dados de um membro")
    public ResponseEntity<MemberResponseDTO> update(@PathVariable Long id, @Valid @RequestBody MemberRequestDTO dto) {
        return ResponseEntity.ok(memberService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remover um membro")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        memberService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
