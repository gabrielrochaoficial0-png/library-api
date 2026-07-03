package com.library.libraryapi.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class LoanRequestDTO {

    @NotNull(message = "O id do livro é obrigatório")
    private Long bookId;

    @NotNull(message = "O id do membro é obrigatório")
    private Long memberId;
}
