package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.exception.ResourceNotFoundException;
import br.com.indra.jp_capacitacao_2026.model.HistoricoPreco;
import br.com.indra.jp_capacitacao_2026.model.Produtos;
import br.com.indra.jp_capacitacao_2026.repository.HistoricoPrecoRepository;
import br.com.indra.jp_capacitacao_2026.repository.ProdutosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProdutosService {

    private final ProdutosRepository produtosRepository;
    private final HistoricoPrecoRepository historicoPrecoRepository;
    private final CategoriaService categoriaService;

    public List<Produtos> getAll() {
        return produtosRepository.findByAtivoTrue();
    }

    public Produtos createdProduto(Produtos produto) {
        if (produto.getAtivo() == null) produto.setAtivo(true);
        if (produto.getQuantidadeEstoque() == null) produto.setQuantidadeEstoque(0);
        // Se vier categoria no body, valida que ela existe
        if (produto.getCategoria() != null && produto.getCategoria().getId() != null) {
            produto.setCategoria(categoriaService.findEntityById(produto.getCategoria().getId()));
        }
        return produtosRepository.save(produto);
    }

    public Produtos atualiza(Long id, Produtos produtoAtualizado) {
        Produtos produto = findById(id);
        produto.setNome(produtoAtualizado.getNome());
        produto.setDescricao(produtoAtualizado.getDescricao());
        produto.setCodigoBarras(produtoAtualizado.getCodigoBarras());
        if (produtoAtualizado.getCategoria() != null && produtoAtualizado.getCategoria().getId() != null) {
            produto.setCategoria(categoriaService.findEntityById(produtoAtualizado.getCategoria().getId()));
        }
        return produtosRepository.save(produto);
    }

    /**
     * CORRECAO: Delete logico implementado.
     * Produto nunca e removido fisicamente do banco — apenas marcado como inativo.
     * Isso preserva o historico de pedidos, precos e transacoes.
     */
    public void deletarProduto(Long id) {
        Produtos produto = findById(id);
        produto.setAtivo(false);
        produtosRepository.save(produto);
    }

    public Produtos getById(Long id) {
        return produtosRepository.findById(id)
                .filter(Produtos::getAtivo)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com id: " + id));
    }

    public List<Produtos> buscarPorNome(String nome) {
        return produtosRepository.findByNomeContainingIgnoreCaseAndAtivoTrue(nome);
    }

    public List<Produtos> buscarPorCategoria(Long categoriaId) {
        return produtosRepository.findByCategoriaIdAndAtivoTrue(categoriaId);
    }

    public Produtos atualizaPreco(Long id, BigDecimal novoPreco) {
        final var produto = findById(id);

        // Registrar historico antes de alterar o preco
        final var historico = new HistoricoPreco();
        historico.setPrecoAntigo(produto.getPreco());
        historico.setProdutos(produto);
        historico.setPrecoNovo(novoPreco);
        historicoPrecoRepository.save(historico);

        produto.setPreco(novoPreco);
        return produtosRepository.saveAndFlush(produto);
    }

    /**
     * Metodo interno usado por outros Services (ex: InventarioService, CarrinhoService).
     * Retorna a entidade sem filtrar por ativo, pois servicos internos podem
     * precisar de acesso mesmo a produtos desativados (ex: historico).
     */
    public Produtos findById(Long id) {
        return produtosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado com id: " + id));
    }
}
