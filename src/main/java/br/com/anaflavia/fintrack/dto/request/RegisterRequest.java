package br.com.anaflavia.fintrack.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Dados necessários para cadastrar um novo usuário")
public class RegisterRequest {

    @Schema(
            description = "Nome do usuário",
            example = "Ana Flávia"
    )
    @NotBlank(message = "O nome é obrigatório")
    @Size(
            max = 120,
            message = "O nome deve possuir no máximo 120 caracteres"
    )
    private String nome;

    @Schema(
            description = "E-mail do usuário",
            example = "ana@email.com"
    )
    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(
            max = 150,
            message = "O e-mail deve possuir no máximo 150 caracteres"
    )
    private String email;

    @Schema(
            description = "Senha do usuário",
            example = "123456",
            minLength = 6
    )
    @NotBlank(message = "A senha é obrigatória")
    @Size(
            min = 6,
            message = "A senha deve possuir no mínimo 6 caracteres"
    )
    private String senha;

    public RegisterRequest() {
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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