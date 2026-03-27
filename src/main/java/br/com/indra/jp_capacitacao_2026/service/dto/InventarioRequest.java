package br.com.indra.jp_capacitacao_2026.service.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InventarioRequest {
    private Integer quantidade;
    private String motivo;
    private String criadoPor;
}
