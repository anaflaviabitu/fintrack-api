package br.com.anaflavia.fintrack.repository;

import br.com.anaflavia.fintrack.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {

    List<Categoria> findByUsuarioId(Long usuarioId);

    Optional<Categoria> findByIdAndUsuarioId(
            Long id,
            Long usuarioId
    );

    boolean existsByNomeIgnoreCaseAndUsuarioId(
            String nome,
            Long usuarioId
    );

    boolean existsByNomeIgnoreCaseAndUsuarioIdAndIdNot(
            String nome,
            Long usuarioId,
            Long categoriaId
    );
}