package br.com.anaflavia.fintrack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta retornada após o cadastro de um usuário")
public class RegisterResponse {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "Ana Flávia")
    private String nome;

    @Schema(description = "E-mail do usuário", example = "ana@email.com")
    private String email;

    @Schema(
            description = "Mensagem referente ao cadastro",
            example = "Usuário cadastrado com sucesso."
    )
    private String mensagem;

    public RegisterResponse() {
    }

    public RegisterResponse(
            Long id,
            String nome,
            String email,
            String mensagem
    ) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.mensagem = mensagem;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getEmail() {
        return email;
    }

    public String getMensagem() {
        return mensagem;
    }
}