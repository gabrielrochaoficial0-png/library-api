package com.library.libraryapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MemberRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "O e-mail deve ser válido")
    private String email;
}
