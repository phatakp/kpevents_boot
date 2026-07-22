package com.phatakp.kpevents.common.auth.filter;

import com.phatakp.kpevents.common.auth.config.ClerkJwksProvider;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ClerkJwtAuthFilter extends OncePerRequestFilter {
    private final ClerkJwksProvider clerkJwksProvider;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Value("${app.clerk.issuer}")
    private String issuer;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
            }

            String token = authHeader.substring(7);
            String clerkUserId = null;
            clerkUserId = getUserIdFromToken(token);


            if (clerkUserId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
//                User user = userService.getUserById(clerkUserId);
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(clerkUserId, null,
                                List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        } catch (Exception ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }
    }

    private String getUserIdFromToken(String token) throws Exception {
        String[] chunks = token.split("\\.");
        String headerJson = new String(Base64.getUrlDecoder().decode(chunks[0]));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode headerNode = mapper.readTree(headerJson);
        String kid = headerNode.get("kid").asText();

        // get correct public key
        PublicKey publicKey = null;
        publicKey = clerkJwksProvider.getPublicKey(kid);


        //Verify token
        Claims claims = Jwts
                .parser()
                .verifyWith(publicKey)
                .clockSkewSeconds(60)
                .requireIssuer(issuer)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.getSubject();
    }
}
