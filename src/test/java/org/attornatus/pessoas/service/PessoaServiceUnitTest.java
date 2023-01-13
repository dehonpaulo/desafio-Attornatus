package org.attornatus.pessoas.service;

import jakarta.persistence.EntityNotFoundException;
import org.attornatus.pessoas.dto.PessoaDTO;
import org.attornatus.pessoas.model.Pessoa;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PessoaServiceUnitTest {
    private Pessoa pessoa1;
    private Pessoa pessoa2;
    private PessoaDTO pessoaDTO1;
    private PessoaDTO pessoaDTO2;

    @Mock
    private PessoaRepository pessoaRepository;

    @InjectMocks
    private PessoaService pessoaService;

    @BeforeEach
    public void setUp() {
        this.pessoa1 = new Pessoa(1L, "João", LocalDate.of(1990, 10, 05));
        this.pessoa2 = new Pessoa(2L, "Maria", LocalDate.of(2000, 01, 25));
        this.pessoaDTO1 = Parser.pessoaParaDTO(pessoa1);
        this.pessoaDTO2 = Parser.pessoaParaDTO(pessoa2);
    }


    @Test
    @DisplayName("Quando chama o método criar, deve salvar no banco de dados uma nova pessoa")
    public void testeMetodoCriar() {
        pessoaDTO1.setId(null);

        Mockito.when(pessoaRepository.save(Mockito.any(Pessoa.class))).thenAnswer(invocationOnMock -> {
            Pessoa pessoa = (Pessoa) invocationOnMock.getArguments()[0];
            pessoa.setId(1L);
            return pessoa;
        });

        PessoaDTO response = pessoaService.criar(pessoaDTO1);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals(pessoaDTO1.getNome(), response.getNome());
        Assertions.assertEquals(pessoaDTO1.getDataDeNascimento(), response.getDataDeNascimento());

        Mockito.verify(pessoaRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    @DisplayName("Quando chama o método consultarPorId, deve retornar uma pessoa")
    public void testeMetodoConsultarPorId() {
        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));

        PessoaDTO response = pessoaService.consultarPorId(1L);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(pessoaDTO1, response);

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Quando chama o método consultarPorId com um id inválido, deve lançar uma exceção")
    public void testeMetodoConsultarPorIdComIdInvalido() {
        Mockito.when(pessoaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());

        Assertions.assertThrows(EntityNotFoundException.class, () -> pessoaService.consultarPorId(1L));

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(1L);
    }

    @Test
    @DisplayName("Quando chama o método consultarTodos, deve retornar todas as pessoas salvas")
    public void testeMetodoConsultarTodos() {
        pessoaDTO1.setId(1L);
        pessoaDTO2.setId(2L);

        Mockito.when(pessoaRepository.findAll(Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(List.of(pessoa1, pessoa2)));

        Page<PessoaDTO> response = pessoaService.consultarTodos(PageRequest.of(1, 20));
        List<PessoaDTO> responseContent = response.getContent();

        Assertions.assertNotNull(response);
        Assertions.assertEquals(2, responseContent.size());
        Assertions.assertEquals(pessoaDTO1, responseContent.get(0));
        Assertions.assertEquals(pessoaDTO2, responseContent.get(1));

        Mockito.verify(pessoaRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }


    @Test
    @DisplayName("Quando chama o método editar, deve salvar a pessoa com as informações alteradas e retornar")
    public void testMetodoEditar() {
        pessoaDTO1.setNome("Pedro");
        pessoaDTO1.setDataDeNascimento(LocalDate.of(2001, 12, 20));

        Mockito.when(pessoaRepository.findById(1L)).thenReturn(Optional.of(pessoa1));
        Mockito.when(pessoaRepository.save(Mockito.any(Pessoa.class))).thenAnswer(invocationOnMock -> {
            Pessoa pessoa = (Pessoa) invocationOnMock.getArguments()[0];
            pessoa.setId(1L);
            return pessoa;
        });

        PessoaDTO response = pessoaService.editar(1L, pessoaDTO1);

        Assertions.assertNotNull(response);
        Assertions.assertEquals(1L, response.getId());
        Assertions.assertEquals(pessoaDTO1.getNome(), response.getNome());
        Assertions.assertEquals(pessoaDTO1.getDataDeNascimento(), response.getDataDeNascimento());

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(pessoaRepository, Mockito.times(1)).save(Mockito.any(Pessoa.class));
    }


    @Test
    @DisplayName("Quando chama o método editar com um id inválido, deve lançar uma exceção")
    public void testeMetodoEditarComIdInvalido() {
        Mockito.when(pessoaRepository.findById(Mockito.anyLong())).thenReturn(Optional.empty());
        Mockito.when(pessoaRepository.save(Mockito.any(Pessoa.class))).thenReturn(null);

        Assertions.assertThrows(EntityNotFoundException.class, () -> pessoaService.editar(1L, pessoaDTO1));

        Mockito.verify(pessoaRepository, Mockito.times(1)).findById(1L);
        Mockito.verify(pessoaRepository, Mockito.times(0)).save(Mockito.any());
    }

}
