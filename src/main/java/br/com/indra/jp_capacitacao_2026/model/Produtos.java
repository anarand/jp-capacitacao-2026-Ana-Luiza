package br.com.indra.jp_capacitacao_2026.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidade Produto atualizada com:
 * - categoria: associação com a entidade Categoria (ManyToOne)
 * - ativo: flag para deleção lógica (o produto nunca é removido do banco)
 * - quantidadeEstoque: quantidade atual em estoque (atualizada via TransacaoEstoque)
 * - criadoEm / atualizadoEm: auditoria automática via Hibernate
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "produtos")
public class Produtos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    private String descricao;

    @Column(nullable = false)
    private BigDecimal preco;

    @Column(name = "codigo_barras")
    private String codigoBarras;

    // NOVO: Controle de estoque
    @Column(name = "quantidade_estoque", nullable = false)
    private Integer quantidadeEstoque = 0;

    // NOVO: Delete logico (nunca deletar fisicamente)
    @Column(nullable = false)
    private Boolean ativo = true;

    // NOVO: Associacao com categoria
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private Categoria categoria;

    // NOVO: Timestamps automaticos
    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
