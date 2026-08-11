package br.com.anaflavia.fintrack.service;

import br.com.anaflavia.fintrack.dto.request.TransacaoRequest;
import br.com.anaflavia.fintrack.dto.response.CategoriaResponse;
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

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;

    public TransacaoService(
            TransacaoRepository transacaoRepository,
            UsuarioRepository usuarioRepository,
            CategoriaRepository categoriaRepository
    ) {
        this.transacaoRepository = transacaoRepository;
        this.usuarioRepository = usuarioRepository;
        this.categoriaRepository = categoriaRepository;
    }


    @Transactional
    public TransacaoResponse cadastrar(
            Long usuarioId,
            TransacaoRequest request
    ) {

        Usuario usuario = buscarUsuario(usuarioId);

        Categoria categoria = buscarCategoriaDoUsuario(
                usuarioId,
                request.getCategoriaId()
        );

        if (request.getTipo() == TipoTransacao.DESPESA) {

            BigDecimal saldoAtual =
                    calcularSaldoValor(usuarioId);

            if (request.getValor().compareTo(saldoAtual) > 0) {
                throw new SaldoInsuficienteException(
                        "Saldo insuficiente para realizar esta despesa."
                );
            }
        }

        Transacao transacao = new Transacao();

        transacao.setDescricao(request.getDescricao());
        transacao.setValor(request.getValor());
        transacao.setData(request.getData());
        transacao.setTipo(request.getTipo());
        transacao.setUsuario(usuario);
        transacao.setCategoria(categoria);

        Transacao salva =
                transacaoRepository.save(transacao);

        return converterParaResponse(salva);
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponse> listar(
            Long usuarioId
    ) {

        buscarUsuario(usuarioId);

        return transacaoRepository
                .findByUsuarioId(usuarioId)
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public TransacaoResponse buscarPorId(
            Long usuarioId,
            Long transacaoId
    ) {

        return converterParaResponse(
                buscarTransacaoDoUsuario(
                        usuarioId,
                        transacaoId
                )
        );
    }


    @Transactional
    public TransacaoResponse atualizar(
            Long usuarioId,
            Long transacaoId,
            TransacaoRequest request
    ) {

        Transacao transacao =
                buscarTransacaoDoUsuario(
                        usuarioId,
                        transacaoId
                );

        Categoria categoria =
                buscarCategoriaDoUsuario(
                        usuarioId,
                        request.getCategoriaId()
                );

        if (request.getTipo() == TipoTransacao.DESPESA) {

            BigDecimal saldoDisponivel =
                    calcularSaldoParaAtualizacao(
                            usuarioId,
                            transacao
                    );

            if (request.getValor().compareTo(saldoDisponivel) > 0) {
                throw new SaldoInsuficienteException(
                        "Saldo insuficiente para atualizar esta despesa."
                );
            }
        }

        transacao.setDescricao(request.getDescricao());
        transacao.setValor(request.getValor());
        transacao.setData(request.getData());
        transacao.setTipo(request.getTipo());
        transacao.setCategoria(categoria);

        Transacao atualizada =
                transacaoRepository.save(transacao);

        return converterParaResponse(atualizada);
    }



    @Transactional
    public void remover(
            Long usuarioId,
            Long transacaoId
    ) {

        Transacao transacao =
                buscarTransacaoDoUsuario(
                        usuarioId,
                        transacaoId
                );

        transacaoRepository.delete(transacao);
    }


    @Transactional(readOnly = true)
    public SaldoResponse consultarSaldo(
            Long usuarioId
    ) {

        buscarUsuario(usuarioId);

        BigDecimal receitas =
                buscarTotalPorTipo(
                        usuarioId,
                        TipoTransacao.RECEITA
                );

        BigDecimal despesas =
                buscarTotalPorTipo(
                        usuarioId,
                        TipoTransacao.DESPESA
                );

        BigDecimal saldo =
                receitas.subtract(despesas);

        return new SaldoResponse(
                receitas,
                despesas,
                saldo
        );
    }

    @Transactional(readOnly = true)
    public List<TransacaoResponse> buscarPorCategoria(
            Long usuarioId,
            Long categoriaId
    ) {

        buscarUsuario(usuarioId);

        buscarCategoriaDoUsuario(
                usuarioId,
                categoriaId
        );

        return transacaoRepository
                .findByUsuarioIdAndCategoriaId(
                        usuarioId,
                        categoriaId
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }


    @Transactional(readOnly = true)
    public List<TransacaoResponse> buscarPorPeriodo(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    ) {

        buscarUsuario(usuarioId);

        if (inicio.isAfter(fim)) {
            throw new IllegalArgumentException(
                    "A data inicial não pode ser posterior à data final."
            );
        }

        return transacaoRepository
                .findByUsuarioIdAndDataBetween(
                        usuarioId,
                        inicio,
                        fim
                )
                .stream()
                .map(this::converterParaResponse)
                .toList();
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

    private Transacao buscarTransacaoDoUsuario(
            Long usuarioId,
            Long transacaoId
    ) {

        return transacaoRepository
                .findByIdAndUsuarioId(
                        transacaoId,
                        usuarioId
                )
                .orElseThrow(
                        () -> new ResourceNotFoundException(
                                "Transação não encontrada."
                        )
                );
    }

    private BigDecimal buscarTotalPorTipo(
            Long usuarioId,
            TipoTransacao tipo
    ) {

        BigDecimal total =
                transacaoRepository
                        .sumValorByUsuarioIdAndTipo(
                                usuarioId,
                                tipo
                        );

        return total != null
                ? total
                : BigDecimal.ZERO;
    }

    private BigDecimal calcularSaldoValor(
            Long usuarioId
    ) {

        BigDecimal receitas =
                buscarTotalPorTipo(
                        usuarioId,
                        TipoTransacao.RECEITA
                );

        BigDecimal despesas =
                buscarTotalPorTipo(
                        usuarioId,
                        TipoTransacao.DESPESA
                );

        return receitas.subtract(despesas);
    }

    private BigDecimal calcularSaldoParaAtualizacao(
            Long usuarioId,
            Transacao transacaoAtual
    ) {

        BigDecimal saldoAtual =
                calcularSaldoValor(usuarioId);

        if (transacaoAtual.getTipo()
                == TipoTransacao.DESPESA) {

            return saldoAtual.add(
                    transacaoAtual.getValor()
            );
        }

        if (transacaoAtual.getTipo()
                == TipoTransacao.RECEITA) {

            return saldoAtual.subtract(
                    transacaoAtual.getValor()
            );
        }

        return saldoAtual;
    }

    private TransacaoResponse converterParaResponse(
            Transacao transacao
    ) {

        CategoriaResponse categoriaResponse =
                new CategoriaResponse(
                        transacao.getCategoria().getId(),
                        transacao.getCategoria().getNome()
                );

        return new TransacaoResponse(
                transacao.getId(),
                transacao.getDescricao(),
                transacao.getValor(),
                transacao.getData(),
                transacao.getTipo(),
                categoriaResponse
        );
    }
}