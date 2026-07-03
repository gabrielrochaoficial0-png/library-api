package com.library.libraryapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookResponseDTO {
    private Long id;
    private String title;
    private String author;
    private String isbn;
    private Integer totalCopies;
    private Integer availableCopies;
}
