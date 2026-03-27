package br.com.indra.jp_capacitacao_2026.service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemCarrinhoRequest {
    private Long produtoId;
    private Integer quantidade;
}
