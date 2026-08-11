package br.com.anaflavia.fintrack.service;

import br.com.anaflavia.fintrack.dto.request.CategoriaRequest;
import br.com.anaflavia.fintrack.dto.response.CategoriaResponse;
import br.com.anaflavia.fintrack.entity.Categoria;
import br.com.anaflavia.fintrack.entity.Usuario;
import br.com.anaflavia.fintrack.exception.CategoriaEmUsoException;
import br.com.anaflavia.fintrack.exception.ResourceNotFoundException;
import br.com.anaflavia.fintrack.repository.CategoriaRepository;
import br.com.anaflavia.fintrack.repository.TransacaoRepository;
import br.com.anaflavia.fintrack.repository.UsuarioRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final TransacaoRepository transacaoRepository;

    public CategoriaService(
            CategoriaRepository categoriaRepository,
            UsuarioRepository usuarioRepository,
            TransacaoRepository transacaoRepository
    ) {
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.transacaoRepository = transacaoRepository;
    }



    @Transactional
    public CategoriaResponse cadastrar(
            Long usuarioId,
            CategoriaRequest request
    ) {

        Usuario usuario = buscarUsuario(usuarioId);

        boolean nomeJaExiste =
                categoriaRepository
                        .existsByNomeIgnoreCaseAndUsuarioId(
                                request.getNome(),
                                usuarioId
                        );

        if (nomeJaExiste) {
            throw new IllegalArgumentException(
                    "Já existe uma categoria com esse nome."
            );
        }

        Categoria categoria = new Categoria();

        categoria.setNome(request.getNome());
        categoria.setUsuario(usuario);

        Categoria categoriaSalva =
                categoriaRepository.save(categoria);

        return converterParaResponse(categoriaSalva);
    }



    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar(
            Long usuarioId
    ) {

        buscarUsuario(usuarioId);

        return categoriaRepository
                .findByUsuarioId(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public CategoriaResponse buscarPorId(
            Long usuarioId,
            Long categoriaId
    ) {

        Categoria categoria =
                buscarCategoriaDoUsuario(
                        usuarioId,
                        categoriaId
                );

        return converterParaResponse(categoria);
    }


    @Transactional
    public CategoriaResponse atualizar(
            Long usuarioId,
            Long categoriaId,
            CategoriaRequest request
    ) {

        Categoria categoria =
                buscarCategoriaDoUsuario(
                        usuarioId,
                        categoriaId
                );

        boolean nomeJaExiste =
                categoriaRepository
                        .existsByNomeIgnoreCaseAndUsuarioIdAndIdNot(
                                request.getNome(),
                                usuarioId,
                                categoriaId
                        );

        if (nomeJaExiste) {
            throw new IllegalArgumentException(
                    "Já existe uma categoria com esse nome."
            );
        }

        categoria.setNome(request.getNome());

        Categoria categoriaAtualizada =
                categoriaRepository.save(categoria);

        return converterParaResponse(
                categoriaAtualizada
        );
    }


    @Transactional
    public void remover(
            Long usuarioId,
            Long categoriaId
    ) {

        Categoria categoria =
                buscarCategoriaDoUsuario(
                        usuarioId,
                        categoriaId
                );

        boolean possuiTransacoes =
                transacaoRepository
                        .existsByUsuarioIdAndCategoriaId(
                                usuarioId,
                                categoriaId
                        );

        if (possuiTransacoes) {
            throw new CategoriaEmUsoException(
                    "Não é possível excluir uma categoria que possui transações vinculadas."
            );
        }

        categoriaRepository.delete(categoria);
    }


    private Usuario buscarUsuario(
            Long usuarioId
    ) {

        return usuarioRepository
                .findById(usuarioId)
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Usuário não encontrado."
                        )
                );
    }

    private Categoria buscarCategoriaDoUsuario(
            Long usuarioId,
            Long categoriaId
    ) {

        return categoriaRepository
                .findByIdAndUsuarioId(
                        categoriaId,
                        usuarioId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Categoria não encontrada."
                        )
                );
    }

    private CategoriaResponse converterParaResponse(
            Categoria categoria
    ) {

        return new CategoriaResponse(
                categoria.getId(),
                categoria.getNome()
        );
    }
}