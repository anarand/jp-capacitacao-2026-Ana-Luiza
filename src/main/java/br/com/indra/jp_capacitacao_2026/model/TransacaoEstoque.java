package br.com.indra.jp_capacitacao_2026.model;

import br.com.indra.jp_capacitacao_2026.model.enums.TipoTransacao;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Registra cada movimentacao de estoque de um produto.
 * E imutavel por design: uma vez criada, nao deve ser alterada.
 *
 * - tipo: ENTRADA, SAIDA, AJUSTE ou DEVOLUCAO
 * - quantidade: unidades movimentadas (sempre positivo)
 * - referenciaId: ID do pedido ou operacao que gerou a transacao (rastreabilidade)
 * - criadoPor: usuario ou sistema que realizou a operacao
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "transacao_estoque")
public class TransacaoEstoque {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private Produtos produto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacao tipo;

    @Column(nullable = false)
    private Integer quantidade;

    private String motivo;

    @Column(name = "referencia_id")
    private String referenciaId;

    @Column(name = "criado_por")
    private String criadoPor;

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;
}
