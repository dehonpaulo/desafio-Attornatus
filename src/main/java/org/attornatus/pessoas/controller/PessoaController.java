package org.attornatus.pessoas.controller;

import jakarta.validation.Valid;
import org.attornatus.pessoas.dto.PessoaDTO;
import org.attornatus.pessoas.service.PessoaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("pessoa")
public class PessoaController {

    @Autowired
    private PessoaService pessoaService;

    @GetMapping("/{id}")
    public ResponseEntity<PessoaDTO> consultarPorId(@PathVariable Long id) {
        PessoaDTO response = pessoaService.consultarPorId(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping
    public ResponseEntity<Page<PessoaDTO>> consultarTodos(Pageable paginacao) {
        Page<PessoaDTO> response = pessoaService.consultarTodos(paginacao);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping
    public ResponseEntity<PessoaDTO> criar(@RequestBody @Valid PessoaDTO pessoaDTO) {
        PessoaDTO response = pessoaService.criar(pessoaDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaDTO> editar(@PathVariable Long id, @RequestBody @Valid PessoaDTO pessoaDTO) {
        PessoaDTO response = pessoaService.editar(id, pessoaDTO);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
