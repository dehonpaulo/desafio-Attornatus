package org.attornatus.pessoas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class PessoaDTO {
    private Long id;
    @NotBlank(message = "Campo nome não pode ser nulo ou estar vazio")
    private String nome;
    @NotNull(message = "Campo dataDeNascimento não pode ser nulo")
    private LocalDate dataDeNascimento;
}
