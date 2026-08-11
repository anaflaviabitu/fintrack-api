package br.com.anaflavia.fintrack.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta retornada após autenticação bem-sucedida")
public class AuthResponse {

    @Schema(
            description = "Token JWT utilizado para acessar os endpoints protegidos",
            example = "eyJhbGciOiJIUzI1NiJ9..."
    )
    private String token;

    @Schema(
            description = "Tipo do token de autenticação",
            example = "Bearer"
    )
    private String tipo;

    @Schema(description = "ID do usuário autenticado", example = "1")
    private Long id;

    @Schema(description = "Nome do usuário", example = "Ana Flávia")
    private String nome;

    @Schema(description = "E-mail do usuário", example = "ana@email.com")
    private String email;

    public AuthResponse() {
    }

    public AuthResponse(
            String token,
            String tipo,
            Long id,
            String nome,
            String email
    ) {
        this.token = token;
        this.tipo = tipo;
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
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
}