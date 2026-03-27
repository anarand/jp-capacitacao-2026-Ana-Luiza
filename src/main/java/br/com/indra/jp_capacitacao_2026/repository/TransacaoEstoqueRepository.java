package br.com.indra.jp_capacitacao_2026.repository;

import br.com.indra.jp_capacitacao_2026.model.TransacaoEstoque;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransacaoEstoqueRepository extends JpaRepository<TransacaoEstoque, Long> {

    // Retorna todas as transacoes de um produto, mais recente primeiro
    List<TransacaoEstoque> findByProdutoIdOrderByCriadoEmDesc(Long produtoId);
}
