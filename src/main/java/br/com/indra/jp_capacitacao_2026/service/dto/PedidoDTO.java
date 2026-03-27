package br.com.indra.jp_capacitacao_2026.service.dto;

import br.com.indra.jp_capacitacao_2026.model.enums.StatusPedido;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PedidoDTO {
    private Long id;
    private String userId;
    private StatusPedido status;
    private BigDecimal total;
    private String endereco;
    private List<ItemPedidoDTO> itens;
    private LocalDateTime criadoEm;
    private LocalDateTime atualizadoEm;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ItemPedidoDTO {
        private Long id;
        private Long produtoId;
        private String produtoNome;
        private Integer quantidade;
        private BigDecimal precoSnapshot;
        private BigDecimal subtotal;
    }
}
