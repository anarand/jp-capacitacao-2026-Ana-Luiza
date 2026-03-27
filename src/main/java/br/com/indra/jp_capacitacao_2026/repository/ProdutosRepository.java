package br.com.indra.jp_capacitacao_2026.repository;

import br.com.indra.jp_capacitacao_2026.model.Produtos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository de Produtos atualizado com metodos de busca derivados do nome do metodo
 * (Spring Data JPA cria a query automaticamente a partir dos nomes).
 */
@Repository
public interface ProdutosRepository extends JpaRepository<Produtos, Long> {

    // Retorna apenas produtos ativos (delete logico)
    List<Produtos> findByAtivoTrue();

    // Busca por nome (case insensitive) entre os ativos
    List<Produtos> findByNomeContainingIgnoreCaseAndAtivoTrue(String nome);

    // Busca por categoria entre os ativos
    List<Produtos> findByCategoriaIdAndAtivoTrue(Long categoriaId);

    // Produtos com estoque abaixo de um valor (para relatorio de estoque baixo)
    List<Produtos> findByQuantidadeEstoqueLessThanAndAtivoTrue(Integer quantidade);
}
