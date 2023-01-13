package org.attornatus.pessoas.service;

import jakarta.persistence.EntityNotFoundException;
import org.attornatus.pessoas.dto.EnderecoDTO;
import org.attornatus.pessoas.exception.BadRequestException;
import org.attornatus.pessoas.model.Endereco;
import org.attornatus.pessoas.model.Pessoa;
import org.attornatus.pessoas.repository.EnderecoRepository;
import org.attornatus.pessoas.repository.PessoaRepository;
import org.attornatus.pessoas.util.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnderecoService {

    @Autowired
    private EnderecoRepository enderecoRepository;
    @Autowired
    private PessoaRepository pessoaRepository;


    public EnderecoDTO criar(EnderecoDTO enderecoDTO) {
        // busca a pessoa à qual pertence o endereço. Caso não haja, lança uma exceção
        Pessoa pessoa = buscarPessoaPeloId(enderecoDTO.getIdPessoa());

        // converte o dto em um Endereco
        Endereco endereco = Parser.dtoParaEndereco(enderecoDTO);
        endereco.setPessoa(pessoa);
        endereco.setPrincipal(false);

        // salva o novo endereço no banco de dados
        Endereco enderecoCriado = enderecoRepository.save(endereco);

        // converte o novo endereço salvo em dto e retorna
        return Parser.enderecoParaDTO(enderecoCriado);
    }


    public List<EnderecoDTO> buscarEnderecosDaPessoa(Long idPessoa) {
        // busca a pessoa pelo id no banco de dados. Caso não haja, lança uma exceção
        Pessoa pessoa = buscarPessoaPeloId(idPessoa);

        // busca todos os endereços da pessoa no banco de dados
        List<Endereco> enderecoList = enderecoRepository.findEnderecosByPessoa(pessoa);

        // transforma a lista de endereços em uma lista de dtos e retorna
        List<EnderecoDTO> enderecoDTOList = enderecoList.stream().map(Parser::enderecoParaDTO).toList();
        return enderecoDTOList;
    }


    public EnderecoDTO escolherEnderecoPrincipal(Long idPessoa, Long idEndereco) {
        // busca a pessoa e o endereço informados. Caso não haja algum deles, lança uma exceção
        Pessoa pessoa = buscarPessoaPeloId(idPessoa);
        Endereco novoEnderecoPrincipal = enderecoRepository.findById(idEndereco).orElseThrow(() -> {
            return new EntityNotFoundException("Não existe nenhum endereco com o id " + idEndereco);
        });

        // vê se o endereço de fato pertence à pessoa informada, evitando inconsistências
        if (!novoEnderecoPrincipal.getPessoa().getId().equals(idPessoa)) {
            throw new BadRequestException("Este endereço não pertence à pessoa informada");
        }

        // busca todos os endereços classificados como "principal" da pessoa informada e os altera ainda em memória,
        // garantindo que não haja mais de um endereço principal
        List<Endereco> enderecosDaPessoa = enderecoRepository.findEnderecosByPessoa(pessoa);
        List<Endereco> enderecosPrincipaisDaPessoa = enderecosDaPessoa.stream().filter(endereco -> {
            return endereco.getPrincipal().equals(true);
        }).map(endereco -> {
            endereco.setPrincipal(false);
            return endereco;
        }).toList();

        // atualiza no banco de dados os endereços que eram classificados como "principal"
        enderecoRepository.saveAll(enderecosPrincipaisDaPessoa);

        // muda em memória o novo endereço escolhido para "principal"
        novoEnderecoPrincipal.setPrincipal(true);
        // salva no banco de dados o novo endereço principal e o retorna como um dto
        return Parser.enderecoParaDTO(enderecoRepository.save(novoEnderecoPrincipal));
    }



    public Pessoa buscarPessoaPeloId(Long id) {
        // busca a pessoa pelo id no banco de dados. Caso não haja, lança uma exceção
        Pessoa pessoa = pessoaRepository.findById(id).orElseThrow(() -> {
            return new EntityNotFoundException("Não existe nenhuma pessoa com o id " + id);
        });
        return pessoa;
    }

}