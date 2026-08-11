package br.com.anaflavia.fintrack.controller;

import br.com.anaflavia.fintrack.dto.request.LoginRequest;
import br.com.anaflavia.fintrack.dto.request.RegisterRequest;
import br.com.anaflavia.fintrack.dto.response.AuthResponse;
import br.com.anaflavia.fintrack.dto.response.RegisterResponse;
import br.com.anaflavia.fintrack.service.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Autenticação",
        description = "Endpoints para cadastro e autenticação de usuários"
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @Operation(
            summary = "Cadastrar usuário",
            description = "Cria uma nova conta de usuário no FinTrack."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Usuário cadastrado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de cadastro inválidos"
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "E-mail já cadastrado"
            )
    })
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> cadastrar(
            @Valid @RequestBody RegisterRequest request
    ) {

        RegisterResponse response =
                authService.cadastrar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @Operation(
            summary = "Realizar login",
            description = "Autentica o usuário e retorna um token JWT."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Login realizado com sucesso"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Dados de login inválidos"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "E-mail ou senha inválidos"
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {

        AuthResponse response =
                authService.login(request);

        return ResponseEntity.ok(response);
    }
}