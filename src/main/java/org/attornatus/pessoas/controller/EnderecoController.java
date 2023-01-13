package org.attornatus.pessoas.controller;

import jakarta.validation.Valid;
import org.attornatus.pessoas.dto.EnderecoDTO;
import org.attornatus.pessoas.dto.IdEnderecoDTO;
import org.attornatus.pessoas.service.EnderecoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("endereco")
public class EnderecoController {

    @Autowired
    private EnderecoService enderecoService;

    @PostMapping
    public ResponseEntity<EnderecoDTO> criar(@RequestBody @Valid EnderecoDTO enderecoDTO) {
        EnderecoDTO response = enderecoService.criar(enderecoDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{idPessoa}")
    public ResponseEntity<List<EnderecoDTO>> buscarEnderecosDaPessoa(@PathVariable Long idPessoa) {
        List<EnderecoDTO> response = enderecoService.buscarEnderecosDaPessoa(idPessoa);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{idPessoa}")
    public ResponseEntity<EnderecoDTO> escolherEnderecoPrincipal(@PathVariable Long idPessoa, @RequestBody IdEnderecoDTO idEnderecoDTO) {
        EnderecoDTO response = enderecoService.escolherEnderecoPrincipal(idPessoa, idEnderecoDTO.getIdEndereco());
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}