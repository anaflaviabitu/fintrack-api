package br.com.anaflavia.fintrack.repository;

import br.com.anaflavia.fintrack.entity.Transacao;
import br.com.anaflavia.fintrack.enums.TipoTransacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransacaoRepository extends JpaRepository<Transacao, Long> {

    List<Transacao> findByUsuarioId(Long usuarioId);

    Optional<Transacao> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    @Query("""
            SELECT t
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
            AND t.categoria.id = :categoriaId
            """)
    List<Transacao> findByUsuarioIdAndCategoriaId(
            Long usuarioId,
            Long categoriaId
    );

    boolean existsByUsuarioIdAndCategoriaId(
            Long usuarioId,
            Long categoriaId
    );

    @Query("""
            SELECT t
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
            AND t.data BETWEEN :inicio AND :fim
            """)
    List<Transacao> findByUsuarioIdAndDataBetween(
            Long usuarioId,
            LocalDate inicio,
            LocalDate fim
    );

    @Query("""
            SELECT COALESCE(SUM(t.valor), 0)
            FROM Transacao t
            WHERE t.usuario.id = :usuarioId
            AND t.tipo = :tipo
            """)
    BigDecimal sumValorByUsuarioIdAndTipo(
            Long usuarioId,
            TipoTransacao tipo
    );
}