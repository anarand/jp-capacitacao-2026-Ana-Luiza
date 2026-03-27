package br.com.indra.jp_capacitacao_2026.repository;

import br.com.indra.jp_capacitacao_2026.model.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    /**
     * Verifica se já existe uma subcategoria com o mesmo nome dentro de um pai.
     * Usado para garantir unicidade do nome no mesmo nível.
     */
    boolean existsByNomeAndCategoriaPaiId(String nome, Long categoriaPaiId);

    /**
     * Verifica se já existe uma categoria raiz (sem pai) com o mesmo nome.
     */
    boolean existsByNomeAndCategoriaPaiIsNull(String nome);
}
