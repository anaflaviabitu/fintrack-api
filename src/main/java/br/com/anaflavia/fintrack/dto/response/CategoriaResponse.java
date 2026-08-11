package br.com.anaflavia.fintrack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados de uma categoria financeira")
public class CategoriaResponse {

    @Schema(description = "ID da categoria", example = "1")
    private Long id;

    @Schema(description = "Nome da categoria", example = "Alimentação")
    private String nome;

    public CategoriaResponse() {
    }

    public CategoriaResponse(Long id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }
}