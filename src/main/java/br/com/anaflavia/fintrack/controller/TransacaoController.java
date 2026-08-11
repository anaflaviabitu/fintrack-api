package br.com.anaflavia.fintrack.controller;

import br.com.anaflavia.fintrack.dto.request.TransacaoRequest;
import br.com.anaflavia.fintrack.dto.response.SaldoResponse;
import br.com.anaflavia.fintrack.dto.response.TransacaoResponse;
import br.com.anaflavia.fintrack.service.TransacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/usuarios/{usuarioId}/transacoes")
@Tag(
        name = "Transações",
        description = "Gerenciamento de receitas, despesas e relatórios financeiros do usuário"
)
@SecurityRequirement(name = "bearerAuth")
public class TransacaoController {

    private final TransacaoService transacaoService;

    public TransacaoController(
            TransacaoService transacaoService
    ) {
        this.transacaoService = transacaoService;
    }


    @Operation(
            summary = "Cadastrar transação",
            description = "Cadastra uma nova receita ou despesa para o usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Transação cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados da transação inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário ou categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Saldo insuficiente para cadastrar a despesa")
    })
    @PostMapping
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<TransacaoResponse> cadastrar(
            @PathVariable Long usuarioId,
            @Valid @RequestBody TransacaoRequest request
    ) {

        TransacaoResponse response =
                transacaoService.cadastrar(
                        usuarioId,
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }


    @Operation(
            summary = "Listar transações",
            description = "Lista todas as transações pertencentes ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transações listadas com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<List<TransacaoResponse>> listar(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                transacaoService.listar(usuarioId)
        );
    }



    @Operation(
            summary = "Buscar transação por ID",
            description = "Busca uma transação específica pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação encontrada"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @GetMapping("/{transacaoId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<TransacaoResponse> buscarPorId(
            @PathVariable Long usuarioId,
            @PathVariable Long transacaoId
    ) {

        return ResponseEntity.ok(
                transacaoService.buscarPorId(
                        usuarioId,
                        transacaoId
                )
        );
    }

    @Operation(
            summary = "Atualizar transação",
            description = "Atualiza os dados de uma transação pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transação atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Transação ou categoria não encontrada"),
            @ApiResponse(responseCode = "409", description = "Saldo insuficiente para atualizar a despesa")
    })
    @PutMapping("/{transacaoId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<TransacaoResponse> atualizar(
            @PathVariable Long usuarioId,
            @PathVariable Long transacaoId,
            @Valid @RequestBody TransacaoRequest request
    ) {

        return ResponseEntity.ok(
                transacaoService.atualizar(
                        usuarioId,
                        transacaoId,
                        request
                )
        );
    }


    @Operation(
            summary = "Excluir transação",
            description = "Remove uma transação pertencente ao usuário autenticado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Transação removida com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Transação não encontrada")
    })
    @DeleteMapping("/{transacaoId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<Void> remover(
            @PathVariable Long usuarioId,
            @PathVariable Long transacaoId
    ) {

        transacaoService.remover(
                usuarioId,
                transacaoId
        );

        return ResponseEntity
                .noContent()
                .build();
    }


    @Operation(
            summary = "Consultar saldo",
            description = "Retorna o total de receitas, despesas e o saldo atual do usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Saldo calculado com sucesso"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/saldo")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<SaldoResponse> consultarSaldo(
            @PathVariable Long usuarioId
    ) {

        return ResponseEntity.ok(
                transacaoService.consultarSaldo(
                        usuarioId
                )
        );
    }


    @Operation(
            summary = "Buscar transações por categoria",
            description = "Lista as transações vinculadas a uma categoria específica do usuário."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transações encontradas"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Categoria não encontrada")
    })
    @GetMapping("/categoria/{categoriaId}")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<List<TransacaoResponse>> buscarPorCategoria(
            @PathVariable Long usuarioId,
            @PathVariable Long categoriaId
    ) {

        return ResponseEntity.ok(
                transacaoService.buscarPorCategoria(
                        usuarioId,
                        categoriaId
                )
        );
    }


    @Operation(
            summary = "Buscar transações por período",
            description = "Lista as transações do usuário entre duas datas."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Transações encontradas"),
            @ApiResponse(responseCode = "400", description = "Período inválido"),
            @ApiResponse(responseCode = "401", description = "Usuário não autenticado"),
            @ApiResponse(responseCode = "403", description = "Acesso não autorizado"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @GetMapping("/periodo")
    @PreAuthorize(
            "@usuarioSecurityService.isOwner(#usuarioId, authentication)"
    )
    public ResponseEntity<List<TransacaoResponse>> buscarPorPeriodo(
            @PathVariable Long usuarioId,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate inicio,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fim
    ) {

        return ResponseEntity.ok(
                transacaoService.buscarPorPeriodo(
                        usuarioId,
                        inicio,
                        fim
                )
        );
    }
}