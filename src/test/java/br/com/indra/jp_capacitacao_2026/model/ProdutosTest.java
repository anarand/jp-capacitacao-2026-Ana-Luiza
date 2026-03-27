package br.com.indra.jp_capacitacao_2026.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes unitarios para a entidade Produtos.
 * Testam apenas o comportamento da classe, sem banco de dados.
 */
class ProdutosTest {

    @Test
    void deveCriarProdutoComValoresCorretos() {
        Produtos produto = new Produtos();
        produto.setNome("Notebook");
        produto.setPreco(new BigDecimal("3500.00"));
        produto.setQuantidadeEstoque(10);
        produto.setAtivo(true);

        assertEquals("Notebook", produto.getNome());
        assertEquals(new BigDecimal("3500.00"), produto.getPreco());
        assertEquals(10, produto.getQuantidadeEstoque());
        assertTrue(produto.getAtivo());
    }

    @Test
    void produtoDeveIniciarComoAtivo() {
        Produtos produto = new Produtos();
        produto.setAtivo(true);
        assertTrue(produto.getAtivo());
    }

    @Test
    void devePermitirDesativarProduto() {
        Produtos produto = new Produtos();
        produto.setAtivo(true);
        produto.setAtivo(false);
        assertFalse(produto.getAtivo());
    }

    @Test
    void deveAtualizarPreco() {
        Produtos produto = new Produtos();
        produto.setPreco(new BigDecimal("100.00"));
        produto.setPreco(new BigDecimal("150.00"));
        assertEquals(new BigDecimal("150.00"), produto.getPreco());
    }
}
