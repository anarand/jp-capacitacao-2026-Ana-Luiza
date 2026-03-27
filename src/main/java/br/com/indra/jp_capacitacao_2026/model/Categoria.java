package br.com.indra.jp_capacitacao_2026.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa uma categoria de produtos com suporte a hierarquia (pai → filhos).
 *
 * - categoriaPai: referência para a categoria superior (null = categoria raiz)
 * - subcategorias: lista de filhos
 * - @JsonIgnore em categoriaPai evita loop infinito na serialização JSON
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "categoria")
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Referência para a categoria pai (auto-relacionamento)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id")
    @JsonIgnore
    private Categoria categoriaPai;

    // Subcategorias filhas
    @OneToMany(mappedBy = "categoriaPai", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Categoria> subcategorias = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "criado_em", updatable = false)
    private LocalDateTime criadoEm;

    @UpdateTimestamp
    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;
}
