package org.attornatus.pessoas.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.attornatus.pessoas.dto.EnderecoDTO;
import org.attornatus.pessoas.dto.IdEnderecoDTO;
import org.attornatus.pessoas.exception.BadRequestException;
import org.attornatus.pessoas.model.Pessoa;
import org.attornatus.pessoas.service.EnderecoService;
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
import java.util.List;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class EnderecoControllerUnitTest {
    private EnderecoDTO enderecoDTO1;
    private EnderecoDTO enderecoDTO2;
    private String enderecoDTOJson1;
    private String enderecoDTOJson2;
    private Pessoa pessoa1;
    private Pessoa pessoa2;
    private IdEnderecoDTO idEnderecoDTO1;
    private String idEnderecoDTOJson1;

    @InjectMocks
    private ExceptionHandlerAdvice exceptionHandlerAdvice;
    @InjectMocks
    private EnderecoController enderecoController;
    @Mock
    private EnderecoService enderecoService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void initMockMvc() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(enderecoController, exceptionHandlerAdvice).build();
        this.objectMapper = new ObjectMapper();
    }

    @BeforeEach
    public void setUp() throws Exception {
        this.enderecoDTO1 = new EnderecoDTO(1L, "Rua 1", "58.100-200", 10, "cidade 1", false, 1L);
        this.enderecoDTO2 = new EnderecoDTO(2L, "Rua 2", "55.432-234", 20, "cidade 2", false, 1L);
        this.enderecoDTOJson1 = objectMapper.writeValueAsString(enderecoDTO1);
        this.enderecoDTOJson2 = objectMapper.writeValueAsString(enderecoDTO2);
        this.pessoa1 = new Pessoa(1L, "João", LocalDate.of(1990, 10, 05));
        this.pessoa2 = new Pessoa(2L, "Maria", LocalDate.of(2000, 01, 25));
        this.idEnderecoDTO1 = new IdEnderecoDTO(1L);
        this.idEnderecoDTOJson1 = objectMapper.writeValueAsString(idEnderecoDTO1);
    }


    @Test
    @DisplayName("Quando chama o método criar, deve retornar o novo endereço criado e o código 201")
    public void testeMetodoCriar() throws Exception {
        Mockito.when(enderecoService.criar(Mockito.any(EnderecoDTO.class))).thenAnswer(invocationOnMock -> {
            EnderecoDTO enderecoDTO = (EnderecoDTO) invocationOnMock.getArguments()[0];
            enderecoDTO.setId(1L);
            return enderecoDTO;
        });

        this.mockMvc.perform(MockMvcRequestBuilders.post("/endereco")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(enderecoDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.logradouro", Matchers.is(enderecoDTO1.getLogradouro())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cep", Matchers.is(enderecoDTO1.getCep())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numero", Matchers.is(enderecoDTO1.getNumero())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cidade", Matchers.is(enderecoDTO1.getCidade())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.principal", Matchers.is(enderecoDTO1.getPrincipal())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idPessoa", Matchers.is(enderecoDTO1.getIdPessoa().intValue())));
    }


    @Test
    @DisplayName("Quando chama o método criar com um idPessoa inválido, retorna 404 Not Found")
    public void testeMetodoCriarComIdPessoaInvalido() throws Exception {
        Mockito.when(enderecoService.criar(Mockito.any())).thenThrow(new EntityNotFoundException("Não existe nenhuma pessoa com o id 1"));

        this.mockMvc.perform(MockMvcRequestBuilders.post("/endereco")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(enderecoDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Quando chama o método buscarEnderecosDaPessoa, retorna uma lista de enderecos")
    public void testeMetodobuscarEnderecosDaPessoa() throws Exception {
        Mockito.when(enderecoService.buscarEnderecosDaPessoa(Mockito.anyLong())).thenReturn(List.of(enderecoDTO1, enderecoDTO2));

        this.mockMvc.perform(MockMvcRequestBuilders.get("/endereco/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", Matchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].logradouro", Matchers.is(enderecoDTO1.getLogradouro())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cep", Matchers.is(enderecoDTO1.getCep())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].numero", Matchers.is(enderecoDTO1.getNumero())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cidade", Matchers.is(enderecoDTO1.getCidade())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].principal", Matchers.is(enderecoDTO1.getPrincipal())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idPessoa", Matchers.is(enderecoDTO1.getIdPessoa().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", Matchers.is(2)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].logradouro", Matchers.is(enderecoDTO2.getLogradouro())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].cep", Matchers.is(enderecoDTO2.getCep())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].numero", Matchers.is(enderecoDTO2.getNumero())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].cidade", Matchers.is(enderecoDTO2.getCidade())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].principal", Matchers.is(enderecoDTO2.getPrincipal())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].idPessoa", Matchers.is(enderecoDTO2.getIdPessoa().intValue())));
    }


    @Test
    @DisplayName("Quando chama o método buscarEnderecosDaPessoa com um idPessoa inválido, retorna 404 Not Found")
    public void testeMetodoBuscarEnderecosDaPessoaComIdPessoaInvalido() throws Exception{
        Mockito.when(enderecoService.buscarEnderecosDaPessoa(1L)).thenThrow(
                new EntityNotFoundException("Não existe nenhuma pessoa com o id 1")
        );

        this.mockMvc.perform(MockMvcRequestBuilders.get("/endereco/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Quando chama o método escolherEnderecoPrincipal, retorna o novo endereço principal e o status ok 200")
    public void testeMetodoEscolherEnderecoPrincipal() throws Exception {
        enderecoDTO1.setPrincipal(true);

        Mockito.when(enderecoService.escolherEnderecoPrincipal(1L, 1L)).thenReturn(enderecoDTO1);

        this.mockMvc.perform(MockMvcRequestBuilders.put("/endereco/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(idEnderecoDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", Matchers.is(enderecoDTO1.getIdPessoa().intValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.logradouro", Matchers.is(enderecoDTO1.getLogradouro())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cep", Matchers.is(enderecoDTO1.getCep())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.numero", Matchers.is(enderecoDTO1.getNumero())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cidade", Matchers.is(enderecoDTO1.getCidade())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.principal", Matchers.is(true)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idPessoa", Matchers.is(enderecoDTO1.getIdPessoa().intValue())));
    }


    @Test
    @DisplayName("Quando chama o método escolherEnderecoPrincipal com um idPessoa ou idEndereco inválido, retorna o status 404 Not Found")
    public void testeMetodoEscolherEnderecoPrincipalComIdPessoaOuIdEnderecoInvalido() throws Exception {
        Mockito.when(enderecoService.escolherEnderecoPrincipal(1L, 1L)).thenThrow(
                new EntityNotFoundException("Não existe nenhuma pessoa/endereco com o id 1")
        );

        this.mockMvc.perform(MockMvcRequestBuilders.put("/endereco/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(idEnderecoDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }


    @Test
    @DisplayName("Quando chama o método escolherEnderecoPrincipal com um idEndereco incompatível com o idPessoa, retorna o status 400 Bad Request")
    public void testeMetodoEscolherEnderecoPrincipalComIdEnderecoInvalido() throws Exception {
        Mockito.when(enderecoService.escolherEnderecoPrincipal(1L, 1L)).thenThrow(
                new BadRequestException("NEste endereço não pertence à pessoa informada")
        );

        this.mockMvc.perform(MockMvcRequestBuilders.put("/endereco/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(idEnderecoDTOJson1))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
