package br.com.anaflavia.fintrack.dto.response;

import br.com.anaflavia.fintrack.enums.TipoTransacao;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados de uma transação financeira")
public class TransacaoResponse {

    @Schema(description = "ID da transação", example = "2")
    private Long id;

    @Schema(
            description = "Descrição da transação",
            example = "Supermercado do mês"
    )
    private String descricao;

    @Schema(description = "Valor da transação", example = "300.00")
    private BigDecimal valor;

    @Schema(description = "Data da transação", example = "2026-08-11")
    private LocalDate data;

    @Schema(
            description = "Tipo da transação",
            example = "DESPESA",
            allowableValues = {"RECEITA", "DESPESA"}
    )
    private TipoTransacao tipo;

    @Schema(description = "Categoria associada à transação")
    private CategoriaResponse categoria;

    public TransacaoResponse() {
    }

    public TransacaoResponse(
            Long id,
            String descricao,
            BigDecimal valor,
            LocalDate data,
            TipoTransacao tipo,
            CategoriaResponse categoria
    ) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.data = data;
        this.tipo = tipo;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getData() {
        return data;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public CategoriaResponse getCategoria() {
        return categoria;
    }
}