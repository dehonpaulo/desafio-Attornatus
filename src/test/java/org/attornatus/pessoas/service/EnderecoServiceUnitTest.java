package org.attornatus.pessoas.service;

import jakarta.persistence.EntityNotFoundException;
import org.attornatus.pessoas.dto.EnderecoDTO;
import org.attornatus.pessoas.exception.BadRequestException;
import org.attornatus.pessoas.model.Endereco;
import org.attornatus.pessoas.model.Pessoa;
import org.attornatus.pessoas.repository.EnderecoRepository;
import org.attornatus.pessoas.repository.PessoaRepository;
import org.attornatus.pessoas.util.Parser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class EnderecoServiceUnitTest {
    private Pessoa pessoa1;
    private Pessoa pessoa2;
    private Endereco endereco1;
    private Endereco endereco2;
    private EnderecoDTO enderecoDTO1;
    private EnderecoDTO enderecoDTO2;

    @Mock
    private EnderecoRepository enderecoRepository;
    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private EnderecoService enderecoService;

    @Test
    @DisplayName("Quando chama o método criar, deve salvar um novo endereço no db")
    public void testeMetodoCriar() {
        Mockito.when(enderecoRepository.save(Mockito.any(Endereco.class))).thenAnswer(invocationOnMock -> {
           Endereco endereco = (Endereco) invocationOnMock.getArguments()[0];
           endereco.setId(1L);
           return endereco;
        });
        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));

        enderecoDTO1.setId(null);

        EnderecoDTO response = enderecoService.criar(enderecoDTO1);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals(enderecoDTO1.getLogradouro(), response.getLogradouro());
        Assertions.assertEquals(enderecoDTO1.getCep(), response.getCep());
        Assertions.assertEquals(enderecoDTO1.getNumero(), response.getNumero());
        Assertions.assertEquals(enderecoDTO1.getCidade(), response.getCidade());
        Assertions.assertEquals(enderecoDTO1.getPrincipal(), response.getPrincipal());
        Assertions.assertEquals(enderecoDTO1.getIdPessoa(), response.getIdPessoa());

        Mockito.verify(enderecoRepository, Mockito.times(1)).save(Mockito.any(Endereco.class));
        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Quando chama o método criar passando um idPessoa inválido, deve lançar uma exceção")
    public void testeMetodoCriarComIdPessoaInvalido() {
        Mockito.when(enderecoRepository.save(Mockito.any(Endereco.class))).thenAnswer(invocationOnMock -> {
            Endereco endereco = (Endereco) invocationOnMock.getArguments()[0];
            endereco.setId(1L);
            return endereco;
        });
        Mockito.when(pessoaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        enderecoDTO1.setId(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> enderecoService.criar(enderecoDTO1));

        Mockito.verify(enderecoRepository, Mockito.times(0)).save(Mockito.any());
        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("Quando chama o método buscarEnderecosDaPessoa, deve retornar todos os endereços buscados")
    public void testeMetodoBuscarEnderecosDaPessoa() {
        Mockito.when(enderecoRepository.findEnderecosByPessoa(pessoa1)).thenReturn(List.of(endereco1, endereco2));
        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));

        List<EnderecoDTO> response = enderecoService.buscarEnderecosDaPessoa(1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, response.size());
        Assertions.assertEquals(enderecoDTO1, response.get(0));
        Assertions.assertEquals(enderecoDTO2, response.get(1));

        Mockito.verify(enderecoRepository, Mockito.times(1)).findEnderecosByPessoa(Mockito.any(Pessoa.class));
        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("Quando chama o método buscarEnderecosDaPessoa com um idPessoa inválido, deve lançar uma exceção")
    public void testeMetodoBuscarEnderecosDaPessoaComIdPessoaInvalido() {
        Mockito.when(enderecoRepository.findEnderecosByPessoa(Mockito.any())).thenReturn(List.of());
        Mockito.when(pessoaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> enderecoService.buscarEnderecosDaPessoa(10L));

        Mockito.verify(enderecoRepository, Mockito.times(0)).findEnderecosByPessoa(Mockito.any());
        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    @DisplayName("Quando chama o método escolherEnderecoPrincipal, deve salvar e retornar o novo endereco principal")
    public void testeMetodoEscolherEnderecoPrincipal() {
        endereco2.setPrincipal(true);
        enderecoDTO1.setPrincipal(true);

        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));
        Mockito.when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco1));
        Mockito.when(enderecoRepository.findEnderecosByPessoa(Mockito.any(Pessoa.class))).thenReturn(List.of(endereco2));
        Mockito.when(enderecoRepository.saveAll(Mockito.any())).thenReturn(null);
        Mockito.when(enderecoRepository.save(Mockito.any(Endereco.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        EnderecoDTO response = enderecoService.escolherEnderecoPrincipal(1L, 1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(enderecoDTO1, response);

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(enderecoRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(enderecoRepository, Mockito.times(1)).findEnderecosByPessoa(Mockito.any());
        Mockito.verify(enderecoRepository, Mockito.times(1)).saveAll(Mockito.any());
        Mockito.verify(enderecoRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Quando chama o método escolherEnderecoPrincipal com um idEndereco inválido, deve lançar uma exceção")
    public void testeMetodoEscolherEnderecoPrincipalComIdEnderecoInvalido() {
        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));
        Mockito.when(enderecoRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(enderecoRepository.findEnderecosByPessoa(Mockito.any())).thenReturn(List.of());
        Mockito.when(enderecoRepository.saveAll(Mockito.any())).thenReturn(null);
        Mockito.when(enderecoRepository.save(Mockito.any(Endereco.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        Assertions.assertThrows(EntityNotFoundException.class, () -> enderecoService.escolherEnderecoPrincipal(1L, 10L));

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(enderecoRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(enderecoRepository, Mockito.times(0)).findEnderecosByPessoa(Mockito.any());
        Mockito.verify(enderecoRepository, Mockito.times(0)).saveAll(Mockito.any());
        Mockito.verify(enderecoRepository, Mockito.times(0)).save(Mockito.any());
    }

    @Test
    @DisplayName("Quando chama o método escolherEnderecoPrincipal com um idEndereco incompatível com idPessoa, lança uma exceção")
    public void testeMetodoEscolherEnderecoPrincipalComEnderecoEPessoaIncompativeis() {
        endereco1.setPessoa(pessoa2);

        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));
        Mockito.when(enderecoRepository.findById(1L)).thenReturn(Optional.of(endereco1));
        Mockito.when(enderecoRepository.findEnderecosByPessoa(Mockito.any(Pessoa.class))).thenReturn(List.of(endereco2));
        Mockito.when(enderecoRepository.saveAll(Mockito.any())).thenReturn(null);
        Mockito.when(enderecoRepository.save(Mockito.any(Endereco.class))).thenAnswer(invocationOnMock -> invocationOnMock.getArguments()[0]);

        Assertions.assertThrows(BadRequestException.class, () -> enderecoService.escolherEnderecoPrincipal(1L, 1L));

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(enderecoRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(enderecoRepository, Mockito.times(0)).findEnderecosByPessoa(Mockito.any());
        Mockito.verify(enderecoRepository, Mockito.times(0)).saveAll(Mockito.any());
        Mockito.verify(enderecoRepository, Mockito.times(0)).save(Mockito.any());
    }

    @BeforeEach
    public void setUp() {
        this.pessoa1 = new Pessoa(1L, "João", LocalDate.of(1990, 10, 05));
        this.pessoa2 = new Pessoa(2L, "Maria", LocalDate.of(2000, 01, 25));
        this.endereco1 = new Endereco(1L, "Rua 1", "58.100-200", 10, "cidade 1", false, pessoa1);
        this.endereco2 = new Endereco(2L, "Rua 2", "55.432-234", 20, "cidade 2", false, pessoa1);
        this.enderecoDTO1 = Parser.enderecoParaDTO(endereco1);
        this.enderecoDTO2 = Parser.enderecoParaDTO(endereco2);
    }
}
