package org.attornatus.pessoas.repository;

import org.attornatus.pessoas.model.Endereco;
import org.attornatus.pessoas.model.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EnderecoRepository extends JpaRepository<Endereco, Long> {
    List<Endereco> findEnderecosByPessoa(Pessoa pessoa);
}
