package br.com.anaflavia.fintrack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "Resumo financeiro do usuário")
public class SaldoResponse {

    @Schema(
            description = "Valor total das receitas",
            example = "3500.00"
    )
    private BigDecimal receitas;

    @Schema(
            description = "Valor total das despesas",
            example = "300.00"
    )
    private BigDecimal despesas;

    @Schema(
            description = "Saldo atual, calculado pelas receitas menos as despesas",
            example = "3200.00"
    )
    private BigDecimal saldo;

    public SaldoResponse() {
    }

    public SaldoResponse(
            BigDecimal receitas,
            BigDecimal despesas,
            BigDecimal saldo
    ) {
        this.receitas = receitas;
        this.despesas = despesas;
        this.saldo = saldo;
    }

    public BigDecimal getReceitas() {
        return receitas;
    }

    public BigDecimal getDespesas() {
        return despesas;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }
}