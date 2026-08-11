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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private TransacaoRepository transacaoRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    private Usuario usuario;
    private Categoria categoria;
    private CategoriaRequest request;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Flávia");
        usuario.setEmail("ana@email.com");

        categoria = new Categoria(
                1L,
                "Alimentação",
                usuario
        );

        request = new CategoriaRequest();
        request.setNome("Alimentação");
    }

    @Test
    void deveCadastrarCategoriaComSucesso() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(
                categoriaRepository
                        .existsByNomeIgnoreCaseAndUsuarioId(
                                "Alimentação",
                                1L
                        )
        ).thenReturn(false);

        when(
                categoriaRepository.save(any(Categoria.class))
        ).thenReturn(categoria);

        CategoriaResponse response =
                categoriaService.cadastrar(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Alimentação",
                response.getNome()
        );

        verify(
                categoriaRepository,
                times(1)
        ).save(any(Categoria.class));
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaJaExistir() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(
                categoriaRepository
                        .existsByNomeIgnoreCaseAndUsuarioId(
                                "Alimentação",
                                1L
                        )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> categoriaService.cadastrar(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Já existe uma categoria com esse nome.",
                exception.getMessage()
        );

        verify(
                categoriaRepository,
                never()
        ).save(any(Categoria.class));
    }

    @Test
    void deveListarCategoriasDoUsuario() {

        when(usuarioRepository.findById(1L))
                .thenReturn(Optional.of(usuario));

        when(
                categoriaRepository.findByUsuarioId(1L)
        ).thenReturn(
                List.of(categoria)
        );

        List<CategoriaResponse> categorias =
                categoriaService.listar(1L);

        assertNotNull(categorias);

        assertEquals(
                1,
                categorias.size()
        );

        assertEquals(
                "Alimentação",
                categorias.get(0).getNome()
        );
    }

    @Test
    void deveBuscarCategoriaPorId() {

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(
                Optional.of(categoria)
        );

        CategoriaResponse response =
                categoriaService.buscarPorId(
                        1L,
                        1L
                );

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Alimentação",
                response.getNome()
        );
    }

    @Test
    void deveLancarExcecaoQuandoCategoriaNaoExistir() {

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        99L,
                        1L
                )
        ).thenReturn(
                Optional.empty()
        );

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> categoriaService.buscarPorId(
                                1L,
                                99L
                        )
                );

        assertEquals(
                "Categoria não encontrada.",
                exception.getMessage()
        );
    }

    @Test
    void deveAtualizarCategoriaComSucesso() {

        CategoriaRequest requestAtualizacao =
                new CategoriaRequest();

        requestAtualizacao.setNome("Mercado");

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(
                Optional.of(categoria)
        );

        when(
                categoriaRepository
                        .existsByNomeIgnoreCaseAndUsuarioIdAndIdNot(
                                "Mercado",
                                1L,
                                1L
                        )
        ).thenReturn(false);

        when(
                categoriaRepository.save(categoria)
        ).thenReturn(categoria);

        CategoriaResponse response =
                categoriaService.atualizar(
                        1L,
                        1L,
                        requestAtualizacao
                );

        assertNotNull(response);

        assertEquals(
                "Mercado",
                response.getNome()
        );

        verify(
                categoriaRepository,
                times(1)
        ).save(categoria);
    }

    @Test
    void deveImpedirNomeDuplicadoNaAtualizacao() {

        CategoriaRequest requestAtualizacao =
                new CategoriaRequest();

        requestAtualizacao.setNome("Salário");

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(
                Optional.of(categoria)
        );

        when(
                categoriaRepository
                        .existsByNomeIgnoreCaseAndUsuarioIdAndIdNot(
                                "Salário",
                                1L,
                                1L
                        )
        ).thenReturn(true);

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> categoriaService.atualizar(
                                1L,
                                1L,
                                requestAtualizacao
                        )
                );

        assertEquals(
                "Já existe uma categoria com esse nome.",
                exception.getMessage()
        );

        verify(
                categoriaRepository,
                never()
        ).save(any(Categoria.class));
    }

    @Test
    void deveRemoverCategoriaSemTransacoes() {

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(
                Optional.of(categoria)
        );

        when(
                transacaoRepository
                        .existsByUsuarioIdAndCategoriaId(
                                1L,
                                1L
                        )
        ).thenReturn(false);

        categoriaService.remover(
                1L,
                1L
        );

        verify(
                categoriaRepository,
                times(1)
        ).delete(categoria);
    }

    @Test
    void deveImpedirExclusaoDeCategoriaComTransacoes() {

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(
                Optional.of(categoria)
        );

        when(
                transacaoRepository
                        .existsByUsuarioIdAndCategoriaId(
                                1L,
                                1L
                        )
        ).thenReturn(true);

        CategoriaEmUsoException exception =
                assertThrows(
                        CategoriaEmUsoException.class,
                        () -> categoriaService.remover(
                                1L,
                                1L
                        )
                );

        assertEquals(
                "Não é possível excluir uma categoria que possui transações vinculadas.",
                exception.getMessage()
        );

        verify(
                categoriaRepository,
                never()
        ).delete(any(Categoria.class));
    }
}