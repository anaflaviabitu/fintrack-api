package br.com.anaflavia.fintrack.service;

import br.com.anaflavia.fintrack.dto.request.TransacaoRequest;
import br.com.anaflavia.fintrack.dto.response.SaldoResponse;
import br.com.anaflavia.fintrack.dto.response.TransacaoResponse;
import br.com.anaflavia.fintrack.entity.Categoria;
import br.com.anaflavia.fintrack.entity.Transacao;
import br.com.anaflavia.fintrack.entity.Usuario;
import br.com.anaflavia.fintrack.enums.TipoTransacao;
import br.com.anaflavia.fintrack.exception.ResourceNotFoundException;
import br.com.anaflavia.fintrack.exception.SaldoInsuficienteException;
import br.com.anaflavia.fintrack.repository.CategoriaRepository;
import br.com.anaflavia.fintrack.repository.TransacaoRepository;
import br.com.anaflavia.fintrack.repository.UsuarioRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {

    @Mock
    private TransacaoRepository transacaoRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private TransacaoService transacaoService;

    private Usuario usuario;
    private Categoria categoriaAlimentacao;
    private Categoria categoriaSalario;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNome("Ana Flávia");
        usuario.setEmail("ana@email.com");

        categoriaAlimentacao = new Categoria(
                1L,
                "Alimentação",
                usuario
        );

        categoriaSalario = new Categoria(
                2L,
                "Salário",
                usuario
        );
    }



    @Test
    void deveCadastrarReceitaComSucesso() {

        TransacaoRequest request = new TransacaoRequest();
        request.setDescricao("Salário");
        request.setValor(new BigDecimal("3500.00"));
        request.setData(LocalDate.of(2026, 8, 11));
        request.setTipo(TipoTransacao.RECEITA);
        request.setCategoriaId(2L);

        when(
                usuarioRepository.findById(1L)
        ).thenReturn(Optional.of(usuario));

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        2L,
                        1L
                )
        ).thenReturn(Optional.of(categoriaSalario));

        Transacao salva = new Transacao(
                1L,
                "Salário",
                new BigDecimal("3500.00"),
                LocalDate.of(2026, 8, 11),
                TipoTransacao.RECEITA,
                usuario,
                categoriaSalario
        );

        when(
                transacaoRepository.save(any(Transacao.class))
        ).thenReturn(salva);

        TransacaoResponse response =
                transacaoService.cadastrar(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Salário",
                response.getDescricao()
        );

        assertEquals(
                new BigDecimal("3500.00"),
                response.getValor()
        );

        assertEquals(
                TipoTransacao.RECEITA,
                response.getTipo()
        );

        assertEquals(
                "Salário",
                response.getCategoria().getNome()
        );

        verify(
                transacaoRepository,
                times(1)
        ).save(any(Transacao.class));
    }



    @Test
    void deveCadastrarDespesaQuandoHouverSaldo() {

        TransacaoRequest request = new TransacaoRequest();
        request.setDescricao("Supermercado");
        request.setValor(new BigDecimal("250.00"));
        request.setData(LocalDate.of(2026, 8, 11));
        request.setTipo(TipoTransacao.DESPESA);
        request.setCategoriaId(1L);

        when(
                usuarioRepository.findById(1L)
        ).thenReturn(Optional.of(usuario));

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(Optional.of(categoriaAlimentacao));

        when(
                transacaoRepository.sumValorByUsuarioIdAndTipo(
                        1L,
                        TipoTransacao.RECEITA
                )
        ).thenReturn(new BigDecimal("3500.00"));

        when(
                transacaoRepository.sumValorByUsuarioIdAndTipo(
                        1L,
                        TipoTransacao.DESPESA
                )
        ).thenReturn(new BigDecimal("0.00"));

        Transacao salva = new Transacao(
                2L,
                "Supermercado",
                new BigDecimal("250.00"),
                LocalDate.of(2026, 8, 11),
                TipoTransacao.DESPESA,
                usuario,
                categoriaAlimentacao
        );

        when(
                transacaoRepository.save(any(Transacao.class))
        ).thenReturn(salva);

        TransacaoResponse response =
                transacaoService.cadastrar(
                        1L,
                        request
                );

        assertNotNull(response);

        assertEquals(
                TipoTransacao.DESPESA,
                response.getTipo()
        );

        assertEquals(
                new BigDecimal("250.00"),
                response.getValor()
        );

        assertEquals(
                "Alimentação",
                response.getCategoria().getNome()
        );

        verify(
                transacaoRepository,
                times(1)
        ).save(any(Transacao.class));
    }

    @Test
    void deveLancarExcecaoQuandoDespesaForMaiorQueSaldo() {

        TransacaoRequest request = new TransacaoRequest();
        request.setDescricao("Compra muito alta");
        request.setValor(new BigDecimal("4000.00"));
        request.setData(LocalDate.of(2026, 8, 11));
        request.setTipo(TipoTransacao.DESPESA);
        request.setCategoriaId(1L);

        when(
                usuarioRepository.findById(1L)
        ).thenReturn(Optional.of(usuario));

        when(
                categoriaRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(Optional.of(categoriaAlimentacao));

        when(
                transacaoRepository.sumValorByUsuarioIdAndTipo(
                        1L,
                        TipoTransacao.RECEITA
                )
        ).thenReturn(new BigDecimal("3500.00"));

        when(
                transacaoRepository.sumValorByUsuarioIdAndTipo(
                        1L,
                        TipoTransacao.DESPESA
                )
        ).thenReturn(new BigDecimal("250.00"));

        SaldoInsuficienteException exception =
                assertThrows(
                        SaldoInsuficienteException.class,
                        () -> transacaoService.cadastrar(
                                1L,
                                request
                        )
                );

        assertEquals(
                "Saldo insuficiente para realizar esta despesa.",
                exception.getMessage()
        );

        verify(
                transacaoRepository,
                never()
        ).save(any(Transacao.class));
    }



    @Test
    void deveCalcularSaldoCorretamente() {

        when(
                usuarioRepository.findById(1L)
        ).thenReturn(Optional.of(usuario));

        when(
                transacaoRepository.sumValorByUsuarioIdAndTipo(
                        1L,
                        TipoTransacao.RECEITA
                )
        ).thenReturn(new BigDecimal("3500.00"));

        when(
                transacaoRepository.sumValorByUsuarioIdAndTipo(
                        1L,
                        TipoTransacao.DESPESA
                )
        ).thenReturn(new BigDecimal("300.00"));

        SaldoResponse response =
                transacaoService.consultarSaldo(1L);

        assertNotNull(response);

        assertEquals(
                new BigDecimal("3500.00"),
                response.getReceitas()
        );

        assertEquals(
                new BigDecimal("300.00"),
                response.getDespesas()
        );

        assertEquals(
                new BigDecimal("3200.00"),
                response.getSaldo()
        );
    }

    @Test
    void deveListarTransacoesDoUsuario() {

        Transacao salario = new Transacao(
                1L,
                "Salário",
                new BigDecimal("3500.00"),
                LocalDate.of(2026, 8, 11),
                TipoTransacao.RECEITA,
                usuario,
                categoriaSalario
        );

        when(
                usuarioRepository.findById(1L)
        ).thenReturn(Optional.of(usuario));

        when(
                transacaoRepository.findByUsuarioId(1L)
        ).thenReturn(
                List.of(salario)
        );

        List<TransacaoResponse> lista =
                transacaoService.listar(1L);

        assertNotNull(lista);

        assertEquals(
                1,
                lista.size()
        );

        assertEquals(
                "Salário",
                lista.get(0).getDescricao()
        );
    }

    @Test
    void deveRemoverTransacaoComSucesso() {

        Transacao transacao = new Transacao(
                1L,
                "Salário",
                new BigDecimal("3500.00"),
                LocalDate.of(2026, 8, 11),
                TipoTransacao.RECEITA,
                usuario,
                categoriaSalario
        );

        when(
                transacaoRepository.findByIdAndUsuarioId(
                        1L,
                        1L
                )
        ).thenReturn(Optional.of(transacao));

        transacaoService.remover(
                1L,
                1L
        );

        verify(
                transacaoRepository,
                times(1)
        ).delete(transacao);
    }

    @Test
    void deveLancarExcecaoQuandoTransacaoNaoExistir() {

        when(
                transacaoRepository.findByIdAndUsuarioId(
                        99L,
                        1L
                )
        ).thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> transacaoService.buscarPorId(
                                1L,
                                99L
                        )
                );

        assertEquals(
                "Transação não encontrada.",
                exception.getMessage()
        );
    }

    @Test
    void deveLancarExcecaoQuandoDataInicialForPosteriorADataFinal() {

        LocalDate inicio =
                LocalDate.of(2026, 8, 31);

        LocalDate fim =
                LocalDate.of(2026, 8, 1);

        when(
                usuarioRepository.findById(1L)
        ).thenReturn(Optional.of(usuario));

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> transacaoService.buscarPorPeriodo(
                                1L,
                                inicio,
                                fim
                        )
                );

        assertEquals(
                "A data inicial não pode ser posterior à data final.",
                exception.getMessage()
        );
    }
}