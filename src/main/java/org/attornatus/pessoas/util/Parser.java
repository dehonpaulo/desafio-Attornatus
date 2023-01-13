package org.attornatus.pessoas.util;

import org.attornatus.pessoas.dto.EnderecoDTO;
import org.attornatus.pessoas.dto.PessoaDTO;
import org.attornatus.pessoas.model.Endereco;
import org.attornatus.pessoas.model.Pessoa;

public class Parser {

    public static PessoaDTO pessoaParaDTO(Pessoa pessoa) {
        return new PessoaDTO(pessoa.getId(), pessoa.getNome(), pessoa.getDataDeNascimento());
    }

    public static Pessoa dtoParaPessoa(PessoaDTO dto) {
        return new Pessoa(null, dto.getNome(), dto.getDataDeNascimento());
    }

    public static EnderecoDTO enderecoParaDTO(Endereco endereco) {
        return new EnderecoDTO(
                endereco.getId(),
                endereco.getLogradouro(),
                endereco.getCep(),
                endereco.getNumero(),
                endereco.getCidade(),
                endereco.getPrincipal(),
                endereco.getPessoa().getId());
    }

    public static Endereco dtoParaEndereco(EnderecoDTO dto) {
        return new Endereco(
                null,
                dto.getLogradouro(),
                dto.getCep(),
                dto.getNumero(),
                dto.getCidade(),
                null,
                null);
    }
}
