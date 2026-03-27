package br.com.indra.jp_capacitacao_2026.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO de resposta para Categoria.
 * Inclui informações do pai e lista simplificada de subcategorias.
 *
 * @JsonInclude(NON_NULL): campos nulos não aparecem no JSON (ex: categoriaPaiId
 * fica oculto se for categoria raiz).
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CategoriaDTO {
    private Long id;
    private String nome;
    private Long categoriaPaiId;
    private String categoriaPaiNome;
    private List<CategoriaDTO> subcategorias;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;
}
