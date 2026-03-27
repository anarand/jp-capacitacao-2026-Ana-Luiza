package br.com.indra.jp_capacitacao_2026.service.dto;

import br.com.indra.jp_capacitacao_2026.model.enums.TipoTransacao;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransacaoEstoqueDTO {
    private Long id;
    private Long produtoId;
    private String produtoNome;
    private TipoTransacao tipo;
    private Integer quantidade;
    private String motivo;
    private String referenciaId;
    private String criadoPor;
    private LocalDateTime criadoEm;
    private Integer estoqueAtualAposTransacao;
}
