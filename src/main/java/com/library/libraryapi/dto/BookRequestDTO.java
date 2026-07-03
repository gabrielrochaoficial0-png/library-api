package com.library.libraryapi.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BookRequestDTO {

    @NotBlank(message = "O título é obrigatório")
    private String title;

    @NotBlank(message = "O autor é obrigatório")
    private String author;

    @NotBlank(message = "O ISBN é obrigatório")
    private String isbn;

    @NotNull(message = "A quantidade total de cópias é obrigatória")
    @Min(value = 1, message = "A quantidade de cópias deve ser no mínimo 1")
    private Integer totalCopies;
}
