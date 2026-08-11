package br.com.anaflavia.fintrack.dto.request;

import br.com.anaflavia.fintrack.enums.TipoTransacao;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

@Schema(description = "Dados necessários para cadastrar ou atualizar uma transação")
public class TransacaoRequest {

    @Schema(
            description = "Descrição da transação",
            example = "Supermercado do mês"
    )
    @NotBlank(message = "A descrição é obrigatória")
    @Size(
            max = 150,
            message = "A descrição deve possuir no máximo 150 caracteres"
    )
    private String descricao;

    @Schema(
            description = "Valor da transação",
            example = "250.00"
    )
    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(
            value = "0.01",
            message = "O valor deve ser maior que zero"
    )
    private BigDecimal valor;

    @Schema(
            description = "Data da transação",
            example = "2026-08-11"
    )
    @NotNull(message = "A data é obrigatória")
    private LocalDate data;

    @Schema(
            description = "Tipo da transação",
            example = "DESPESA",
            allowableValues = {"RECEITA", "DESPESA"}
    )
    @NotNull(message = "O tipo da transação é obrigatório")
    private TipoTransacao tipo;

    @Schema(
            description = "ID da categoria associada à transação",
            example = "1"
    )
    @NotNull(message = "A categoria é obrigatória")
    private Long categoriaId;

    public TransacaoRequest() {
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public TipoTransacao getTipo() {
        return tipo;
    }

    public void setTipo(TipoTransacao tipo) {
        this.tipo = tipo;
    }

    public Long getCategoriaId() {
        return categoriaId;
    }

    public void setCategoriaId(Long categoriaId) {
        this.categoriaId = categoriaId;
    }
}