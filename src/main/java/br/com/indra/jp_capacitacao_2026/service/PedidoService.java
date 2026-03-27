package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.exception.RegraDeNegocioException;
import br.com.indra.jp_capacitacao_2026.exception.ResourceNotFoundException;
import br.com.indra.jp_capacitacao_2026.model.*;
import br.com.indra.jp_capacitacao_2026.model.enums.StatusPedido;
import br.com.indra.jp_capacitacao_2026.repository.PedidoRepository;
import br.com.indra.jp_capacitacao_2026.service.dto.PedidoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final CarrinhoService carrinhoService;
    private final InventarioService inventarioService;

    /**
     * CHECKOUT: converte o carrinho ativo em pedido.
     *
     * Passos:
     * 1. Busca o carrinho ATIVO do usuario
     * 2. Valida que o carrinho nao esta vazio
     * 3. Para cada item, verifica e desconta o estoque
     * 4. Cria o Pedido com os itens (copiando precoSnapshot do carrinho)
     * 5. Finaliza o carrinho (status -> FINALIZADO)
     *
     * A anotacao @Transactional garante que tudo isso aconteca atomicamente:
     * se alguma etapa falhar (ex: estoque insuficiente), TUDO e revertido.
     */
    @Transactional
    public PedidoDTO checkout(String userId, String endereco) {
        Carrinho carrinho = carrinhoService.getCarrinhoAtivoEntity(userId);

        if (carrinho.getItens().isEmpty()) {
            throw new RegraDeNegocioException("Nao e possivel fazer checkout com o carrinho vazio.");
        }

        // Cria o pedido base
        Pedido pedido = Pedido.builder()
                .userId(userId)
                .status(StatusPedido.CRIADO)
                .total(carrinho.getTotal())
                .endereco(endereco)
                .build();

        pedido = pedidoRepository.save(pedido);

        // Cria os itens do pedido e desconta o estoque de cada produto
        final Pedido pedidoFinal = pedido;
        List<ItemPedido> itensPedido = carrinho.getItens().stream()
                .map(itemCarrinho -> {
                    Produtos produto = itemCarrinho.getProduto();

                    // Desconta estoque — lanca EstoqueInsuficienteException se necessario
                    inventarioService.removerEstoqueInterno(
                            produto,
                            itemCarrinho.getQuantidade(),
                            "PEDIDO-" + pedidoFinal.getId()
                    );

                    return ItemPedido.builder()
                            .pedido(pedidoFinal)
                            .produto(produto)
                            .quantidade(itemCarrinho.getQuantidade())
                            .precoSnapshot(itemCarrinho.getPrecoSnapshot())
                            .build();
                })
                .collect(Collectors.toList());

        pedido.setItens(itensPedido);
        pedidoRepository.save(pedido);

        // Finaliza o carrinho
        carrinhoService.finalizarCarrinho(carrinho);

        return toDTO(pedido);
    }

    public PedidoDTO getById(Long id) {
        return toDTO(findEntityById(id));
    }

    public List<PedidoDTO> getPedidosByUsuario(String userId) {
        return pedidoRepository.findByUserIdOrderByCriadoEmDesc(userId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Cancela um pedido.
     * Regra: so e possivel cancelar se status for CRIADO ou PAGO.
     * Ao cancelar, o estoque de cada item e devolvido.
     */
    @Transactional
    public PedidoDTO cancelar(Long id) {
        Pedido pedido = findEntityById(id);

        if (!pedido.getStatus().podeCancelar()) {
            throw new RegraDeNegocioException(
                "Nao e possivel cancelar pedido com status " + pedido.getStatus() +
                ". Apenas pedidos CRIADO ou PAGO podem ser cancelados."
            );
        }

        // Devolver estoque de cada item
        pedido.getItens().forEach(item ->
            inventarioService.devolverEstoqueInterno(
                    item.getProduto(),
                    item.getQuantidade(),
                    "CANCELAMENTO-PEDIDO-" + pedido.getId()
            )
        );

        pedido.setStatus(StatusPedido.CANCELADO);
        return toDTO(pedidoRepository.save(pedido));
    }

    /**
     * Avanca o status do pedido para o proximo estado.
     * Utiliza a maquina de estados do enum StatusPedido para validar a transicao.
     */
    @Transactional
    public PedidoDTO atualizarStatus(Long id, StatusPedido novoStatus) {
        Pedido pedido = findEntityById(id);

        if (!pedido.getStatus().podeTransicionarPara(novoStatus)) {
            throw new RegraDeNegocioException(
                "Transicao invalida: " + pedido.getStatus() + " -> " + novoStatus
            );
        }

        pedido.setStatus(novoStatus);
        return toDTO(pedidoRepository.save(pedido));
    }

    private Pedido findEntityById(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado com id: " + id));
    }

    private PedidoDTO toDTO(Pedido pedido) {
        return PedidoDTO.builder()
                .id(pedido.getId())
                .userId(pedido.getUserId())
                .status(pedido.getStatus())
                .total(pedido.getTotal())
                .endereco(pedido.getEndereco())
                .criadoEm(pedido.getCriadoEm())
                .atualizadoEm(pedido.getAtualizadoEm())
                .itens(pedido.getItens().stream()
                        .map(item -> PedidoDTO.ItemPedidoDTO.builder()
                                .id(item.getId())
                                .produtoId(item.getProduto().getId())
                                .produtoNome(item.getProduto().getNome())
                                .quantidade(item.getQuantidade())
                                .precoSnapshot(item.getPrecoSnapshot())
                                .subtotal(item.getPrecoSnapshot().multiply(BigDecimal.valueOf(item.getQuantidade())))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }
}
