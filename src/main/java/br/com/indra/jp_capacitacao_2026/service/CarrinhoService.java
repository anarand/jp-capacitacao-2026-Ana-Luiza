package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.exception.RegraDeNegocioException;
import br.com.indra.jp_capacitacao_2026.exception.ResourceNotFoundException;
import br.com.indra.jp_capacitacao_2026.model.Carrinho;
import br.com.indra.jp_capacitacao_2026.model.ItemCarrinho;
import br.com.indra.jp_capacitacao_2026.model.Produtos;
import br.com.indra.jp_capacitacao_2026.model.enums.StatusCarrinho;
import br.com.indra.jp_capacitacao_2026.repository.CarrinhoRepository;
import br.com.indra.jp_capacitacao_2026.repository.ItemCarrinhoRepository;
import br.com.indra.jp_capacitacao_2026.service.dto.CarrinhoDTO;
import br.com.indra.jp_capacitacao_2026.service.dto.ItemCarrinhoRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarrinhoService {

    private final CarrinhoRepository carrinhoRepository;
    private final ItemCarrinhoRepository itemCarrinhoRepository;
    private final ProdutosService produtosService;

    /**
     * Retorna o carrinho ATIVO do usuario.
     * Se nao existir, cria um automaticamente.
     * Regra: so pode existir 1 carrinho ativo por usuario.
     */
    @Transactional
    public CarrinhoDTO getOuCriarCarrinho(String userId) {
        Carrinho carrinho = carrinhoRepository
                .findByUserIdAndStatus(userId, StatusCarrinho.ATIVO)
                .orElseGet(() -> {
                    Carrinho novo = Carrinho.builder()
                            .userId(userId)
                            .status(StatusCarrinho.ATIVO)
                            .total(BigDecimal.ZERO)
                            .build();
                    return carrinhoRepository.save(novo);
                });
        return toDTO(carrinho);
    }

    /**
     * Adiciona um produto ao carrinho.
     * - Se o produto ja existe no carrinho, incrementa a quantidade.
     * - Captura o preco atual como precoSnapshot.
     * - Recalcula o total.
     */
    @Transactional
    public CarrinhoDTO adicionarItem(String userId, ItemCarrinhoRequest request) {
        Carrinho carrinho = getCarrinhoAtivoEntity(userId);
        Produtos produto = produtosService.getById(request.getProdutoId());

        // Se produto ja esta no carrinho, incrementa quantidade
        ItemCarrinho item = itemCarrinhoRepository
                .findByCarrinhoIdAndProdutoId(carrinho.getId(), produto.getId())
                .map(existente -> {
                    existente.setQuantidade(existente.getQuantidade() + request.getQuantidade());
                    return existente;
                })
                .orElseGet(() -> ItemCarrinho.builder()
                        .carrinho(carrinho)
                        .produto(produto)
                        .quantidade(request.getQuantidade())
                        .precoSnapshot(produto.getPreco()) // captura preco atual
                        .build());

        itemCarrinhoRepository.save(item);

        // Recarrega itens e recalcula total
        Carrinho carrinhoAtualizado = carrinhoRepository.findById(carrinho.getId()).get();
        carrinhoAtualizado.recalcularTotal();
        carrinhoRepository.save(carrinhoAtualizado);

        return toDTO(carrinhoAtualizado);
    }

    /**
     * Atualiza a quantidade de um item.
     * Se quantidade for 0 ou negativa, remove o item.
     */
    @Transactional
    public CarrinhoDTO atualizarItem(String userId, Long itemId, Integer novaQuantidade) {
        Carrinho carrinho = getCarrinhoAtivoEntity(userId);

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item nao encontrado com id: " + itemId));

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new RegraDeNegocioException("Este item nao pertence ao seu carrinho.");
        }

        if (novaQuantidade <= 0) {
            itemCarrinhoRepository.delete(item);
        } else {
            item.setQuantidade(novaQuantidade);
            itemCarrinhoRepository.save(item);
        }

        Carrinho carrinhoAtualizado = carrinhoRepository.findById(carrinho.getId()).get();
        carrinhoAtualizado.recalcularTotal();
        carrinhoRepository.save(carrinhoAtualizado);

        return toDTO(carrinhoAtualizado);
    }

    /**
     * Remove um item especifico do carrinho.
     */
    @Transactional
    public CarrinhoDTO removerItem(String userId, Long itemId) {
        Carrinho carrinho = getCarrinhoAtivoEntity(userId);

        ItemCarrinho item = itemCarrinhoRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Item nao encontrado com id: " + itemId));

        if (!item.getCarrinho().getId().equals(carrinho.getId())) {
            throw new RegraDeNegocioException("Este item nao pertence ao seu carrinho.");
        }

        itemCarrinhoRepository.delete(item);

        Carrinho carrinhoAtualizado = carrinhoRepository.findById(carrinho.getId()).get();
        carrinhoAtualizado.recalcularTotal();
        carrinhoRepository.save(carrinhoAtualizado);

        return toDTO(carrinhoAtualizado);
    }

    /**
     * Metodo interno usado pelo PedidoService no momento do checkout.
     * Finaliza o carrinho (muda status para FINALIZADO).
     */
    public Carrinho getCarrinhoAtivoEntity(String userId) {
        return carrinhoRepository
                .findByUserIdAndStatus(userId, StatusCarrinho.ATIVO)
                .orElseThrow(() -> new ResourceNotFoundException("Nenhum carrinho ativo encontrado para o usuario: " + userId));
    }

    public void finalizarCarrinho(Carrinho carrinho) {
        carrinho.setStatus(StatusCarrinho.FINALIZADO);
        carrinhoRepository.save(carrinho);
    }

    private CarrinhoDTO toDTO(Carrinho carrinho) {
        return CarrinhoDTO.builder()
                .id(carrinho.getId())
                .userId(carrinho.getUserId())
                .status(carrinho.getStatus())
                .total(carrinho.getTotal())
                .criadoEm(carrinho.getCriadoEm())
                .atualizadoEm(carrinho.getAtualizadoEm())
                .itens(carrinho.getItens().stream()
                        .map(item -> CarrinhoDTO.ItemCarrinhoDTO.builder()
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
