package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.exception.RegraDeNegocioException;
import br.com.indra.jp_capacitacao_2026.model.Carrinho;
import br.com.indra.jp_capacitacao_2026.model.ItemCarrinho;
import br.com.indra.jp_capacitacao_2026.model.Produtos;
import br.com.indra.jp_capacitacao_2026.model.enums.StatusCarrinho;
import br.com.indra.jp_capacitacao_2026.repository.CarrinhoRepository;
import br.com.indra.jp_capacitacao_2026.repository.ItemCarrinhoRepository;
import br.com.indra.jp_capacitacao_2026.service.dto.CarrinhoDTO;
import br.com.indra.jp_capacitacao_2026.service.dto.ItemCarrinhoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testes unitarios para CarrinhoService usando Mockito.
 * Nao sobe o contexto Spring — apenas testa a logica de negocio.
 */
@ExtendWith(MockitoExtension.class)
class CarrinhoServiceTest {

    @Mock
    private CarrinhoRepository carrinhoRepository;

    @Mock
    private ItemCarrinhoRepository itemCarrinhoRepository;

    @Mock
    private ProdutosService produtosService;

    @InjectMocks
    private CarrinhoService carrinhoService;

    private Produtos produto;
    private Carrinho carrinho;

    @BeforeEach
    void setUp() {
        produto = new Produtos();
        produto.setId(1L);
        produto.setNome("Produto Teste");
        produto.setPreco(new BigDecimal("50.00"));
        produto.setAtivo(true);
        produto.setQuantidadeEstoque(10);

        carrinho = new Carrinho();
        carrinho.setId(1L);
        carrinho.setUserId("usuario-01");
        carrinho.setStatus(StatusCarrinho.ATIVO);
        carrinho.setTotal(BigDecimal.ZERO);
        carrinho.setItens(new ArrayList<>());
    }

    @Test
    void deveCriarCarrinhoQuandoNaoExistir() {
        when(carrinhoRepository.findByUserIdAndStatus("usuario-01", StatusCarrinho.ATIVO))
                .thenReturn(Optional.empty());
        when(carrinhoRepository.save(any(Carrinho.class))).thenReturn(carrinho);

        CarrinhoDTO resultado = carrinhoService.getOuCriarCarrinho("usuario-01");

        assertNotNull(resultado);
        assertEquals("usuario-01", resultado.getUserId());
        verify(carrinhoRepository, times(1)).save(any(Carrinho.class));
    }

    @Test
    void deveRetornarCarrinhoExistenteQuandoJaAtivo() {
        when(carrinhoRepository.findByUserIdAndStatus("usuario-01", StatusCarrinho.ATIVO))
                .thenReturn(Optional.of(carrinho));

        CarrinhoDTO resultado = carrinhoService.getOuCriarCarrinho("usuario-01");

        assertNotNull(resultado);
        assertEquals(StatusCarrinho.ATIVO, resultado.getStatus());
        // Nao deve salvar novo carrinho
        verify(carrinhoRepository, never()).save(any(Carrinho.class));
    }

    @Test
    void deveLancarExcecaoAoRemoverItemDeOutroCarrinho() {
        Carrinho outroCarrinho = new Carrinho();
        outroCarrinho.setId(99L);
        outroCarrinho.setUserId("outro-usuario");
        outroCarrinho.setStatus(StatusCarrinho.ATIVO);
        outroCarrinho.setItens(new ArrayList<>());

        ItemCarrinho item = new ItemCarrinho();
        item.setId(10L);
        item.setCarrinho(outroCarrinho); // item pertence a outro carrinho

        when(carrinhoRepository.findByUserIdAndStatus("usuario-01", StatusCarrinho.ATIVO))
                .thenReturn(Optional.of(carrinho));
        when(itemCarrinhoRepository.findById(10L)).thenReturn(Optional.of(item));

        assertThrows(RegraDeNegocioException.class,
                () -> carrinhoService.removerItem("usuario-01", 10L));
    }

    @Test
    void deveCalcularTotalCorretamenteAoAdicionarItem() {
        ItemCarrinho item = new ItemCarrinho();
        item.setId(1L);
        item.setCarrinho(carrinho);
        item.setProduto(produto);
        item.setQuantidade(2);
        item.setPrecoSnapshot(new BigDecimal("50.00"));

        carrinho.getItens().add(item);
        carrinho.recalcularTotal();

        assertEquals(new BigDecimal("100.00"), carrinho.getTotal());
    }
}
