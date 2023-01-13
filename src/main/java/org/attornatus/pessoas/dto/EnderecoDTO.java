package org.attornatus.pessoas.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EnderecoDTO {

    private Long id;
    @NotBlank
    private String logradouro;
    @NotBlank
    private String cep;
    @NotNull
    private Integer numero;
    @NotBlank
    private String cidade;
    private Boolean principal;
    @NotNull
    private Long idPessoa;
}
