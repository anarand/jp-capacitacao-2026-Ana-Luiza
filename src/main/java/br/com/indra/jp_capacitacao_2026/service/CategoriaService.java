package br.com.indra.jp_capacitacao_2026.service;

import br.com.indra.jp_capacitacao_2026.exception.RegraDeNegocioException;
import br.com.indra.jp_capacitacao_2026.exception.ResourceNotFoundException;
import br.com.indra.jp_capacitacao_2026.model.Categoria;
import br.com.indra.jp_capacitacao_2026.repository.CategoriaRepository;
import br.com.indra.jp_capacitacao_2026.service.dto.CategoriaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    public List<CategoriaDTO> getAll() {
        return categoriaRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public CategoriaDTO getById(Long id) {
        return toDTO(findEntityById(id));
    }

    public CategoriaDTO criar(CategoriaDTO dto) {
        validarNomeUnico(dto.getNome(), dto.getCategoriaPaiId(), null);

        Categoria categoria = new Categoria();
        categoria.setNome(dto.getNome());

        if (dto.getCategoriaPaiId() != null) {
            Categoria pai = findEntityById(dto.getCategoriaPaiId());
            categoria.setCategoriaPai(pai);
        }

        return toDTO(categoriaRepository.save(categoria));
    }

    public CategoriaDTO atualizar(Long id, CategoriaDTO dto) {
        Categoria categoria = findEntityById(id);
        validarNomeUnico(dto.getNome(), dto.getCategoriaPaiId(), id);

        categoria.setNome(dto.getNome());

        if (dto.getCategoriaPaiId() != null) {
            if (dto.getCategoriaPaiId().equals(id)) {
                throw new RegraDeNegocioException("Uma categoria nao pode ser pai de si mesma.");
            }
            Categoria pai = findEntityById(dto.getCategoriaPaiId());
            categoria.setCategoriaPai(pai);
        } else {
            categoria.setCategoriaPai(null);
        }

        return toDTO(categoriaRepository.save(categoria));
    }

    public void deletar(Long id) {
        Categoria categoria = findEntityById(id);
        categoriaRepository.delete(categoria);
    }

    /**
     * Valida que o nome da categoria e unico dentro do mesmo nivel hierarquico.
     * Regra: nome unico entre irmaos (mesmo pai).
     */
    private void validarNomeUnico(String nome, Long paiId, Long idParaIgnorar) {
        boolean existe;
        if (paiId == null) {
            existe = categoriaRepository.existsByNomeAndCategoriaPaiIsNull(nome);
        } else {
            existe = categoriaRepository.existsByNomeAndCategoriaPaiId(nome, paiId);
        }

        if (existe) {
            throw new RegraDeNegocioException(
                "Ja existe uma categoria com o nome '" + nome + "' nesse nivel hierarquico."
            );
        }
    }

    /**
     * Metodo interno reutilizado por outros Services para buscar a entidade Categoria.
     */
    public Categoria findEntityById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada com id: " + id));
    }

    public CategoriaDTO toDTO(Categoria categoria) {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNome(categoria.getNome());
        dto.setCriadoEm(categoria.getCriadoEm());
        dto.setAtualizadoEm(categoria.getAtualizadoEm());

        if (categoria.getCategoriaPai() != null) {
            dto.setCategoriaPaiId(categoria.getCategoriaPai().getId());
            dto.setCategoriaPaiNome(categoria.getCategoriaPai().getNome());
        }

        if (categoria.getSubcategorias() != null && !categoria.getSubcategorias().isEmpty()) {
            dto.setSubcategorias(categoria.getSubcategorias().stream()
                    .map(sub -> CategoriaDTO.builder()
                            .id(sub.getId())
                            .nome(sub.getNome())
                            .build())
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}
