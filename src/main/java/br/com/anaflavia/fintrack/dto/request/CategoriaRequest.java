package br.com.anaflavia.fintrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastrar ou atualizar uma categoria")
public class CategoriaRequest {

    @Schema(
            description = "Nome da categoria",
            example = "Alimentação"
    )
    @NotBlank(message = "O nome da categoria é obrigatório")
    @Size(max = 80, message = "O nome deve possuir no máximo 80 caracteres")
    private String nome;

    public CategoriaRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}