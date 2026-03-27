package br.com.indra.jp_capacitacao_2026.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Representa um item dentro do carrinho.
 *
 * - precoSnapshot: preco do produto NO MOMENTO em que foi adicionado ao carrinho.
 *   Isso e essencial para que alteracoes futuras de preco nao aftem carrinho em andamento.
 *
 * - @JsonIgnore no carrinho evita loop infinito (Carrinho -> ItemCarrinho -> Carrinho)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "item_carrinho")
public class ItemCarrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carrinho_id", nullable = false)
    private Carrinho carrinho;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @Column(nullable = false)
    private Integer quantidade;

    /**
     * Preco capturado no momento em que o item foi adicionado.
     * Nao se atualiza se o produto mudar de preco depois.
     */
    @Column(name = "preco_snapshot", nullable = false)
    private BigDecimal precoSnapshot;
}
