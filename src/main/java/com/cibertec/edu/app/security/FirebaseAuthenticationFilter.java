package com.cibertec.edu.app.security;

import com.cibertec.edu.app.entity.Usuario;
import com.cibertec.edu.app.enums.Rol;
import com.cibertec.edu.app.repository.UsuarioRepository;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FirebaseAuthenticationFilter extends OncePerRequestFilter {

    private final UsuarioRepository usuarioRepository;
    private final FirebaseService firebaseService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);
        if (token != null) {
            FirebaseToken firebaseToken = firebaseService.verifyIdToken(token);
            if (firebaseToken != null) {
                Usuario usuario = findOrCreateUsuario(firebaseToken);
                List<GrantedAuthority> authorities = List.of(
                        new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(usuario, null, authorities);
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private Usuario findOrCreateUsuario(FirebaseToken firebaseToken) {
        String uid = firebaseToken.getUid();
        String email = firebaseToken.getEmail();

        Optional<Usuario> byUid = usuarioRepository.findByFirebaseUid(uid);
        if (byUid.isPresent()) {
            return byUid.get();
        }

        if (email != null) {
            Optional<Usuario> byEmail = usuarioRepository.findByEmail(email);
            if (byEmail.isPresent()) {
                Usuario usuario = byEmail.get();
                usuario.setFirebaseUid(uid);
                return usuarioRepository.save(usuario);
            }
        }

        Usuario nuevo = Usuario.builder()
                .firebaseUid(uid)
                .nombre(resolveNombre(firebaseToken, email))
                .email(email)
                .fechaRegistro(LocalDateTime.now())
                .activo(true)
                .rol(Rol.VECINO)
                .build();
        return usuarioRepository.save(nuevo);
    }

    private String resolveNombre(FirebaseToken firebaseToken, String email) {
        if (StringUtils.hasText(firebaseToken.getName())) {
            return firebaseToken.getName();
        }
        if (StringUtils.hasText(email) && email.contains("@")) {
            return email.substring(0, email.indexOf('@'));
        }
        return firebaseToken.getUid();
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
