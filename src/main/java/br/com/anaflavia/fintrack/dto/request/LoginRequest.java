package br.com.anaflavia.fintrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados necessários para autenticação do usuário")
public class LoginRequest {

    @Schema(
            description = "E-mail do usuário",
            example = "ana@email.com"
    )
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @Schema(
            description = "Senha do usuário",
            example = "123456"
    )
    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    public LoginRequest() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
}