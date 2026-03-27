package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.exception.EstoqueInsuficienteException;
import br.com.indra.jp_capacitacao_2026.model.Produtos;
import br.com.indra.jp_capacitacao_2026.model.TransacaoEstoque;
import br.com.indra.jp_capacitacao_2026.model.enums.TipoTransacao;
import br.com.indra.jp_capacitacao_2026.repository.ProdutosRepository;
import br.com.indra.jp_capacitacao_2026.repository.TransacaoEstoqueRepository;
import br.com.indra.jp_capacitacao_2026.service.dto.TransacaoEstoqueDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventarioService {

    private final ProdutosRepository produtosRepository;
    private final TransacaoEstoqueRepository transacaoEstoqueRepository;
    private final ProdutosService produtosService;

    private static final int ESTOQUE_MINIMO = 5;

    /**
     * Registra uma ENTRADA de estoque (ex: reposicao de fornecedor).
     * Incrementa quantidadeEstoque no produto.
     */
    @Transactional
    public TransacaoEstoqueDTO adicionarEstoque(Long produtoId, Integer quantidade, String motivo, String criadoPor) {
        Produtos produto = produtosService.findById(produtoId);

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtosRepository.save(produto);

        TransacaoEstoque transacao = TransacaoEstoque.builder()
                .produto(produto)
                .tipo(TipoTransacao.ENTRADA)
                .quantidade(quantidade)
                .motivo(motivo)
                .criadoPor(criadoPor)
                .build();

        transacaoEstoqueRepository.save(transacao);
        return toDTO(transacao, produto.getQuantidadeEstoque());
    }

    /**
     * Registra uma SAIDA de estoque (ex: venda).
     * Lanca EstoqueInsuficienteException se nao houver quantidade suficiente.
     * Inclui flag de alerta se estoque atingir o minimo apos a operacao.
     */
    @Transactional
    public TransacaoEstoqueDTO removerEstoque(Long produtoId, Integer quantidade, String motivo, String criadoPor) {
        Produtos produto = produtosService.findById(produtoId);

        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(
                "Estoque insuficiente para o produto '" + produto.getNome() +
                "'. Disponivel: " + produto.getQuantidadeEstoque() + ", solicitado: " + quantidade
            );
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtosRepository.save(produto);

        TransacaoEstoque transacao = TransacaoEstoque.builder()
                .produto(produto)
                .tipo(TipoTransacao.SAIDA)
                .quantidade(quantidade)
                .motivo(motivo)
                .criadoPor(criadoPor)
                .build();

        transacaoEstoqueRepository.save(transacao);

        TransacaoEstoqueDTO dto = toDTO(transacao, produto.getQuantidadeEstoque());

        // Notificacao de estoque minimo via flag no DTO
        if (produto.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
            dto.setMotivo("[ALERTA: ESTOQUE BAIXO - " + produto.getQuantidadeEstoque() + " unidades restantes] " + motivo);
        }

        return dto;
    }

    /**
     * Metodo interno usado pelo checkout de pedidos.
     * Nao gera DTO pois e chamado dentro de uma transacao maior.
     */
    @Transactional
    public void removerEstoqueInterno(Produtos produto, Integer quantidade, String referenciaId) {
        if (produto.getQuantidadeEstoque() < quantidade) {
            throw new EstoqueInsuficienteException(
                "Estoque insuficiente para o produto '" + produto.getNome() +
                "'. Disponivel: " + produto.getQuantidadeEstoque() + ", solicitado: " + quantidade
            );
        }

        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - quantidade);
        produtosRepository.save(produto);

        TransacaoEstoque transacao = TransacaoEstoque.builder()
                .produto(produto)
                .tipo(TipoTransacao.SAIDA)
                .quantidade(quantidade)
                .motivo("Saida por pedido")
                .referenciaId(referenciaId)
                .criadoPor("SISTEMA")
                .build();

        transacaoEstoqueRepository.save(transacao);
    }

    /**
     * Metodo interno para devolver estoque ao cancelar pedido.
     */
    @Transactional
    public void devolverEstoqueInterno(Produtos produto, Integer quantidade, String referenciaId) {
        produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() + quantidade);
        produtosRepository.save(produto);

        TransacaoEstoque transacao = TransacaoEstoque.builder()
                .produto(produto)
                .tipo(TipoTransacao.DEVOLUCAO)
                .quantidade(quantidade)
                .motivo("Devolucao por cancelamento de pedido")
                .referenciaId(referenciaId)
                .criadoPor("SISTEMA")
                .build();

        transacaoEstoqueRepository.save(transacao);
    }

    public List<TransacaoEstoqueDTO> getHistorico(Long produtoId) {
        return transacaoEstoqueRepository.findByProdutoIdOrderByCriadoEmDesc(produtoId)
                .stream()
                .map(t -> toDTO(t, null))
                .collect(Collectors.toList());
    }

    public List<Produtos> getProdutosComEstoqueBaixo() {
        return produtosRepository.findByQuantidadeEstoqueLessThanAndAtivoTrue(ESTOQUE_MINIMO);
    }

    private TransacaoEstoqueDTO toDTO(TransacaoEstoque t, Integer estoqueAtual) {
        return TransacaoEstoqueDTO.builder()
                .id(t.getId())
                .produtoId(t.getProduto().getId())
                .produtoNome(t.getProduto().getNome())
                .tipo(t.getTipo())
                .quantidade(t.getQuantidade())
                .motivo(t.getMotivo())
                .referenciaId(t.getReferenciaId())
                .criadoPor(t.getCriadoPor())
                .criadoEm(t.getCriadoEm())
                .estoqueAtualAposTransacao(estoqueAtual)
                .build();
    }
}
