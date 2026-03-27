package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.model.HistoricoPreco;
import br.com.indra.jp_capacitacao_2026.repository.HistoricoPrecoRepository;
import br.com.indra.jp_capacitacao_2026.service.dto.HistoricoProdutoDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HistoricoService {

    private final HistoricoPrecoRepository historicoPrecoRepository;

    /**
     * CORRECAO DO BUG ORIGINAL:
     *
     * Problemas no codigo original:
     * 1. O flatMap estava vazio: .stream().flatMap(/* desenvolveria o mapeamento *\/)
     * 2. O codigo tentava chamar .getId(), .getProdutos(), etc. diretamente
     *    em um Set<HistoricoPreco> (que e uma colecao, nao um objeto unico).
     * 3. O retorno era HistoricoProdutoDTO (objeto unico) mas a entrada e um Set (varios registros).
     *
     * SOLUCAO:
     * - Retornar List<HistoricoProdutoDTO>
     * - Mapear cada elemento do Set individualmente com .stream().map()
     * - Usar o builder do DTO (correto pois a classe tem @Builder)
     */
    public List<HistoricoProdutoDTO> getHistoricoByProdutoId(Long produtoId) {
        return historicoPrecoRepository.findByProdutosId(produtoId)
                .stream()
                .map(historico -> HistoricoProdutoDTO.builder()
                        .id(historico.getId())
                        .produto(historico.getProdutos().getNome())
                        .precoAntigo(historico.getPrecoAntigo())
                        .precoNovo(historico.getPrecoNovo())
                        .dataRegistro(historico.getDataAlteracao())
                        .build())
                .collect(Collectors.toList());
    }
}
