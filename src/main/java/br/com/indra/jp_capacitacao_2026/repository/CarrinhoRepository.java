package br.com.indra.jp_capacitacao_2026.repository;

import br.com.indra.jp_capacitacao_2026.model.Carrinho;
import br.com.indra.jp_capacitacao_2026.model.enums.StatusCarrinho;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CarrinhoRepository extends JpaRepository<Carrinho, Long> {

    /**
     * Busca o carrinho ATIVO de um usuario.
     * Regra: so pode existir 1 carrinho ativo por usuario.
     */
    Optional<Carrinho> findByUserIdAndStatus(String userId, StatusCarrinho status);

    /**
     * Verifica se o usuario ja tem um carrinho ativo (para evitar duplicidade).
     */
    boolean existsByUserIdAndStatus(String userId, StatusCarrinho status);
}
