package com.registeration.demo.security.Filters;

import com.auth0.jwt.algorithms.Algorithm;
import com.registeration.demo.security.JwtService.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {


    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final String secretKey;
    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtils jwtUtils, String secretKey) {
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
        this.secretKey= secretKey;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authenticationToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        User user = (User) authResult.getPrincipal();
       // secretKey = env.getProperty("spring.jwt.secret-key");
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        String access_token = jwtUtils.createToken(user.getUsername(), user.getAuthorities(), request.getRequestURI().toString());
        String refresh_token =jwtUtils.refreshToken(user.getUsername(), request.getRequestURI().toString());
        jwtUtils.writeOutputStream(response,"access_token","refresh_token", access_token, refresh_token );
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }
}
