package com.samanthamaiaduarte.todoapi.infra.security;

import com.samanthamaiaduarte.todoapi.exception.ApiNoTokenException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenExpiredException;
import com.samanthamaiaduarte.todoapi.exception.ApiTokenInvalidException;
import com.samanthamaiaduarte.todoapi.infra.exceptionhandler.CustomAuthenticationEntryPoint;
import com.samanthamaiaduarte.todoapi.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    private TokenService tokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CustomAuthenticationEntryPoint entryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var token = this.recoverToken(request);
        if(token != null) {
            try {
                var login = tokenService.validateToken(token);
                UserDetails user = userRepository.findByLogin(login);

                if (user == null) throw new ApiTokenInvalidException("User not found.");

                var authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (ApiTokenExpiredException | ApiTokenInvalidException exception) {
                entryPoint.commence(request, response, exception);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        return authHeader == null ? null : authHeader.replace("Bearer ", "");
    }
}
