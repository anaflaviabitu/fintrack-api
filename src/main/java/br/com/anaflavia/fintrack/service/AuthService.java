package br.com.anaflavia.fintrack.service;

import br.com.anaflavia.fintrack.dto.request.LoginRequest;
import br.com.anaflavia.fintrack.dto.request.RegisterRequest;
import br.com.anaflavia.fintrack.dto.response.AuthResponse;
import br.com.anaflavia.fintrack.dto.response.RegisterResponse;
import br.com.anaflavia.fintrack.entity.Usuario;
import br.com.anaflavia.fintrack.exception.CredenciaisInvalidasException;
import br.com.anaflavia.fintrack.exception.EmailJaCadastradoException;
import br.com.anaflavia.fintrack.repository.UsuarioRepository;
import br.com.anaflavia.fintrack.security.jwt.JwtService;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }


    public RegisterResponse cadastrar(RegisterRequest request) {

        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new EmailJaCadastradoException(
                    "E-mail já cadastrado."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNome(request.getNome());
        usuario.setEmail(request.getEmail());

        usuario.setSenha(
                passwordEncoder.encode(
                        request.getSenha()
                )
        );

        usuario = usuarioRepository.save(usuario);

        return new RegisterResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                "Usuário cadastrado com sucesso."
        );
    }


    public AuthResponse login(LoginRequest request) {

        Usuario usuario = usuarioRepository
                .findByEmail(request.getEmail())
                .orElseThrow(
                        () -> new CredenciaisInvalidasException(
                                "E-mail ou senha inválidos."
                        )
                );

        boolean senhaCorreta =
                passwordEncoder.matches(
                        request.getSenha(),
                        usuario.getSenha()
                );

        if (!senhaCorreta) {
            throw new CredenciaisInvalidasException(
                    "E-mail ou senha inválidos."
            );
        }

        String token =
                jwtService.gerarToken(
                        usuario.getId(),
                        usuario.getEmail()
                );

        return new AuthResponse(
                token,
                "Bearer",
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}