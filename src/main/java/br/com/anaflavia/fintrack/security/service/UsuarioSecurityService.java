package br.com.anaflavia.fintrack.security.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service("usuarioSecurityService")
public class UsuarioSecurityService {

    public boolean isOwner(
            Long usuarioId,
            Authentication authentication
    ) {

        if (authentication == null) {
            return false;
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Jwt jwt)) {
            return false;
        }

        Object claimUsuarioId =
                jwt.getClaim("usuarioId");

        if (claimUsuarioId == null) {
            return false;
        }

        Long usuarioIdDoToken =
                Long.valueOf(claimUsuarioId.toString());

        return usuarioId.equals(usuarioIdDoToken);
    }
}