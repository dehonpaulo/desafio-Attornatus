package org.attornatus.pessoas.service;

import jakarta.persistence.EntityNotFoundException;
import org.attornatus.pessoas.dto.PessoaDTO;
import org.attornatus.pessoas.model.Pessoa;
import org.attornatus.pessoas.repository.PessoaRepository;
import org.attornatus.pessoas.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PessoaService {
    @Autowired
    private PessoaRepository pessoaRepository;


    public PessoaDTO criar(PessoaDTO pessoaDTO) {
        // converte o DTO para um objeto do tipo Pessoa e salva no banco de dados
        Pessoa pessoa = Parser.dtoParaPessoa(pessoaDTO);
        Pessoa pessoaSalva = pessoaRepository.save(pessoa);

        // converte a pessoa salva para DTO e o retorna
        return Parser.pessoaParaDTO(pessoaSalva);
    }


    public PessoaDTO consultarPorId(Long id) {
        // buscar uma pessoa no banco de dados pelo id. Caso não haja, lança uma exceção
        Pessoa pessoaEncontrada = pessoaRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Não existe nenhuma pessoa com o id " + id);
        });

        // converte a pessoa encontrada para DTO e o retorna
        return Parser.pessoaParaDTO(pessoaEncontrada);
    }

    public Page<PessoaDTO> consultarTodos(Pageable paginacao) {
        // busca todas as pessoas no banco de dados, as converte para DTO e retorna de forma paginada
        return pessoaRepository.findAll(paginacao).map(Parser::pessoaParaDTO);
    }


    public PessoaDTO editar(Long id, PessoaDTO pessoaDTO) {
        // busca a pessoa no banco de dados pelo id. Caso não haja, lança uma exceção
        Pessoa pessoa = pessoaRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Não existe nenhuma pessoa com o id " + id);
        });

        // altera apenas os campos com novos valores informados
        if(pessoaDTO.getNome() != null) pessoa.setNome(pessoaDTO.getNome());
        if(pessoaDTO.getDataDeNascimento() != null) pessoa.setDataDeNascimento(pessoaDTO.getDataDeNascimento());

        // salva a alteração no banco de dados
        Pessoa pessoaEditada = pessoaRepository.save(pessoa);

        // converte a pessoa alterada para DTO e retorna
        return Parser.pessoaParaDTO(pessoaEditada);
    }
}