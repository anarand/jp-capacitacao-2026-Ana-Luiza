package br.com.indra.jp_capacitacao_2026.model;

import br.com.indra.jp_capacitacao_2026.model.enums.StatusCarrinho;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa o carrinho de compras de um usuario.
 *
 * Regra de negocio: cada usuario so pode ter 1 carrinho ATIVO por vez.
 * Essa regra e validada no CarrinhoService.
 *
 * - itens: lista de ItemCarrinho (CascadeType.ALL para salvar/remover junto)
 * - total: calculado automaticamente ao adicionar/remover itens
 * - userId: identificador do usuario (simulado como String, seria substituido por User entity com Spring Security)
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "carrinho")
public class Carrinho {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCarrinho status;

    @OneToMany(mappedBy = "carrinho", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ItemCarrinho> itens = new ArrayList<>();

    @Column(nullable = false)
    @Builder.Default
    private BigDecimal total = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    /**
     * Recalcula o total somando (preco * quantidade) de cada item.
     * Deve ser chamado sempre que um item e adicionado, atualizado ou removido.
     */
    public void recalcularTotal() {
        this.total = itens.stream()
                .map(item -> item.getPrecoSnapshot().multiply(BigDecimal.valueOf(item.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
