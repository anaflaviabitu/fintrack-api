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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    private Usuario usuario;

    @BeforeEach
    void setUp() {

        usuario = new Usuario();

        usuario.setNome("Ana Flávia");
        usuario.setEmail("ana@email.com");
        usuario.setSenha("senhaCriptografada");
    }

    @Test
    void deveCadastrarUsuarioComSucesso() {

        RegisterRequest request = new RegisterRequest();

        request.setNome("Ana Flávia");
        request.setEmail("ana@email.com");
        request.setSenha("123456");

        when(
                usuarioRepository.existsByEmail(
                        request.getEmail()
                )
        ).thenReturn(false);

        when(
                passwordEncoder.encode(
                        request.getSenha()
                )
        ).thenReturn("senhaCriptografada");

        when(
                usuarioRepository.save(any(Usuario.class))
        ).thenAnswer(invocation -> {

            Usuario usuarioSalvo =
                    invocation.getArgument(0);

            usuarioSalvo.setId(1L);

            return usuarioSalvo;
        });

        RegisterResponse response =
                authService.cadastrar(request);

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Ana Flávia",
                response.getNome()
        );

        assertEquals(
                "ana@email.com",
                response.getEmail()
        );

        assertEquals(
                "Usuário cadastrado com sucesso.",
                response.getMensagem()
        );

        verify(
                usuarioRepository,
                times(1)
        ).save(any(Usuario.class));
    }

    @Test
    void deveLancarExcecaoQuandoEmailJaEstiverCadastrado() {

        RegisterRequest request = new RegisterRequest();

        request.setNome("Ana Flávia");
        request.setEmail("ana@email.com");
        request.setSenha("123456");

        when(
                usuarioRepository.existsByEmail(
                        request.getEmail()
                )
        ).thenReturn(true);

        assertThrows(
                EmailJaCadastradoException.class,
                () -> authService.cadastrar(request)
        );

        verify(
                usuarioRepository,
                never()
        ).save(any(Usuario.class));
    }

    @Test
    void deveRealizarLoginComSucesso() {

        LoginRequest request = new LoginRequest();

        request.setEmail("ana@email.com");
        request.setSenha("123456");

        usuario.setId(1L);

        when(
                usuarioRepository.findByEmail(
                        request.getEmail()
                )
        ).thenReturn(Optional.of(usuario));

        when(
                passwordEncoder.matches(
                        request.getSenha(),
                        usuario.getSenha()
                )
        ).thenReturn(true);

        when(
                jwtService.gerarToken(
                        usuario.getId(),
                        usuario.getEmail()
                )
        ).thenReturn("token-jwt-teste");

        AuthResponse response =
                authService.login(request);

        assertNotNull(response);

        assertEquals(
                "token-jwt-teste",
                response.getToken()
        );

        assertEquals(
                "Bearer",
                response.getTipo()
        );

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "Ana Flávia",
                response.getNome()
        );

        assertEquals(
                "ana@email.com",
                response.getEmail()
        );
    }

    @Test
    void deveLancarExcecaoQuandoSenhaEstiverIncorreta() {

        LoginRequest request = new LoginRequest();

        request.setEmail("ana@email.com");
        request.setSenha("senhaErrada");

        usuario.setId(1L);

        when(
                usuarioRepository.findByEmail(
                        request.getEmail()
                )
        ).thenReturn(Optional.of(usuario));

        when(
                passwordEncoder.matches(
                        request.getSenha(),
                        usuario.getSenha()
                )
        ).thenReturn(false);

        assertThrows(
                CredenciaisInvalidasException.class,
                () -> authService.login(request)
        );

        verify(
                jwtService,
                never()
        ).gerarToken(
                anyLong(),
                anyString()
        );
    }
}