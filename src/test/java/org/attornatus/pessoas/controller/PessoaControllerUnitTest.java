package org.attornatus.pessoas.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityNotFoundException;
import org.attornatus.pessoas.dto.PessoaDTO;
import org.attornatus.pessoas.service.PessoaService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PessoaControllerUnitTest {
    private PessoaDTO pessoaDTO1;
    private PessoaDTO pessoaDTO2;
    private String pessoaDTOJson1;
    private String pessoaDTOJson2;
    private String pessoaDTOJsonVazio;

    @InjectMocks
    private ExceptionHandlerAdvice exceptionHandlerAdvice;
    @InjectMocks
    private PessoaController pessoaController;
    @Mock
    private PessoaService pessoaService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void initMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(pessoaController, exceptionHandlerAdvice).build();
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    }

    @BeforeEach
    public void setUp() throws JsonProcessingException {
        this.pessoaDTO1 = new PessoaDTO(1L, "João", LocalDate.of(1990, 10, 05));
        this.pessoaDTO2 = new PessoaDTO(2L, "Maria", LocalDate.of(2000, 01, 25));
        this.pessoaDTOJson1 = objectMapper.writeValueAsString(pessoaDTO1);
        this.pessoaDTOJson2 = objectMapper.writeValueAsString(pessoaDTO2);
        this.pessoaDTOJsonVazio = objectMapper.writeValueAsString(new PessoaDTO());
    }


    @Test
    @DisplayName("Quando chama o método criar, deve retornar o objeto criado com o código 201")
    public void testeMetodoCriar() throws Exception {
        Mockito.when(pessoaService.criar(Mockito.any(PessoaDTO.class))).thenAnswer(invocationOnMock -> {
            PessoaDTO pessoaDTO = (PessoaDTO) invocationOnMock.getArguments()[0];
            pessoaDTO.setId(1L);
            return pessoaDTO;
        });

        this.mockMvc.perform(MockMvcRequestBuilders.post("/pessoa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", Matchers.is(pessoaDTO1.getNome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[0]", Matchers.is(pessoaDTO1.getDataDeNascimento().getYear())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[1]", Matchers.is(pessoaDTO1.getDataDeNascimento().getMonthValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[2]", Matchers.is(pessoaDTO1.getDataDeNascimento().getDayOfMonth())));
    }


    @Test
    @DisplayName("Quando chama o método criar com campos nulos, deve retornar o status 400 Bad Request")
    public void testeMetodoCriarComCamposNulos() throws Exception {
        Mockito.when(pessoaService.criar(Mockito.any(PessoaDTO.class))).thenAnswer(invocationOnMock -> {
            PessoaDTO pessoaDTO = (PessoaDTO) invocationOnMock.getArguments()[0];
            pessoaDTO.setId(1L);
            return pessoaDTO;
        });

        this.mockMvc.perform(MockMvcRequestBuilders.post("/pessoa")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaDTOJsonVazio))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                // deve haver uma mensagem de erro para cada um dos dois campos, ou seja, duas mensagens
                .andExpect(MockMvcResultMatchers.jsonPath("$.message.length()", Matchers.is(2)));
    }

    @Test
    @DisplayName("Quando chama o método consultarPorId, retorna a pessoa procurada e o status 200 ok")
    public void testeMetodoConsultarPorId() throws Exception {
        Mockito.when(pessoaService.consultarPorId(1L)).thenReturn(pessoaDTO1);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/pessoa/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", Matchers.is(pessoaDTO1.getNome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[0]", Matchers.is(pessoaDTO1.getDataDeNascimento().getYear())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[1]", Matchers.is(pessoaDTO1.getDataDeNascimento().getMonthValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[2]", Matchers.is(pessoaDTO1.getDataDeNascimento().getDayOfMonth())));
    }


    @Test
    @DisplayName("Quando chama o método consultarPorId com um id inválido, retorna o status 404 Not Found")
    public void testeMetodoConsultarPorIdComIdInvalido() throws Exception {
        Mockito.when(pessoaService.consultarPorId(1L)).thenThrow(
                new EntityNotFoundException("Não existe uma pessoa com o id 1")
        );

        this.mockMvc.perform(MockMvcRequestBuilders.get("/pessoa/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Quando chama o método editar, deve retornar o objeto atualizado com o código 200")
    public void testeMetodoEditar() throws Exception {
        Mockito.when(pessoaService.editar(Mockito.anyLong(), Mockito.any(PessoaDTO.class))).thenAnswer(invocationOnMock -> {
            PessoaDTO pessoaDTO = (PessoaDTO) invocationOnMock.getArguments()[1];
            pessoaDTO.setId((Long) invocationOnMock.getArguments()[0]);
            return pessoaDTO;
        });

        this.mockMvc.perform(MockMvcRequestBuilders.put("/pessoa/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome", Matchers.is(pessoaDTO1.getNome())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[0]", Matchers.is(pessoaDTO1.getDataDeNascimento().getYear())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[1]", Matchers.is(pessoaDTO1.getDataDeNascimento().getMonthValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.dataDeNascimento[2]", Matchers.is(pessoaDTO1.getDataDeNascimento().getDayOfMonth())));
    }


    @Test
    @DisplayName("Quando chama o método editar com um id inválido, deve retornar o código 404")
    public void testeMetodoEditarComIdInvalido() throws Exception {
        Mockito.when(pessoaService.editar(Mockito.anyLong(), Mockito.any(PessoaDTO.class))).thenThrow(
                new EntityNotFoundException("Não existe nenhuma pessoa com o id 1")
        );

        this.mockMvc.perform(MockMvcRequestBuilders.put("/pessoa/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pessoaDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}