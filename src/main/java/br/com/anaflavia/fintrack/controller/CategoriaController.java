package br.com.anaflavia.fintrack.controller;

import br.com.anaflavia.fintrack.dto.request.CategoriaRequest;
import br.com.anaflavia.fintrack.dto.response.CategoriaResponse;
import br.com.anaflavia.fintrack.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/{usuarioId}/categorias")
@Tag(
        name = "Categorias",
        description = "Gerenciamento das categorias financeiras do usuário"
)
@SecurityRequirement(name = "bearerAuth")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(
            summary = "Cadastrar categoria",
            description = "Cria uma nova categoria para o usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Categoria criada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "409", description = "Categoria já cadastrada")
    })
    @PostMapping
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<CategoriaResponse> cadastrar(
            @PathVariable Long usuarioId,
            @Valid @RequestBody CategoriaRequest request
    ) {

        CategoriaResponse response =
                categoriaService.cadastrar(usuarioId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Listar categorias",
            description = "Lista todas as categorias pertencentes ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categorias encontradas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado")
    })
    @GetMapping
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<List<CategoriaResponse>> listar(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                categoriaService.listar(usuarioId)
        );
    }

    @Operation(
            summary = "Buscar categoria",
            description = "Busca uma categoria específica pertencente ao usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria encontrada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/{categoriaId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<CategoriaResponse> buscarPorId(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId
    ) {

        return ResponseEntity.ok(
                categoriaService.buscarPorId(
                        usuarioId,
                        categoriaId
                )
        );
    }

    @Operation(
            summary = "Atualizar categoria",
            description = "Atualiza uma categoria pertencente ao usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Categoria atualizada"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @PutMapping("/{categoriaId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<CategoriaResponse> atualizar(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId,
            @Valid @RequestBody CategoriaRequest request
    ) {

        return ResponseEntity.ok(
                categoriaService.atualizar(
                        usuarioId,
                        categoriaId,
                        request
                )
        );
    }

    @Operation(
            summary = "Excluir categoria",
            description = "Remove uma categoria pertencente ao usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Categoria removida"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada"),
            @ApiResponse(
                    responseCode = "409",
                    description = "Categoria possui transações vinculadas"
            )
    })
    @DeleteMapping("/{categoriaId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<Void> remover(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId
    ) {

        categoriaService.remover(
                usuarioId,
                categoriaId
        );

        return ResponseEntity.noContent().build();
    }
}