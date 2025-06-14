package com.example.demo.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.cert.CertificateException;

@Component
@RequiredArgsConstructor
//OncePerRequestFilter ensures this filter runs only once per request.
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    //Inside JwtService it extract details from token and validates it
    private final JwtService jwtService;
    // UserDetailsService loads user details (from DB or another service)
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            //Passes to next filter(FilterSecurityInterceptor). from there it decides to pass the request (if permitAll) or block (if authenticated)
            filterChain.doFilter(request,response);
            return;
        }
        jwt = authHeader.substring(7);
      userEmail = jwtService.extractUserName(jwt);
      

      //check the userEmail is not null and checks SecurityContextHolder no user is authenticated yet for this request
      //There might be other filters before or after your filter that already set the authentication. it prevents accidental overriding of existing valid auth info
      if (userEmail !=  null && SecurityContextHolder.getContext().getAuthentication() == null){
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            if (jwtService.isTokenValid(jwt,userDetails)){
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails,null,userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                //@PreAuthorize / @Secured / hasRole() : These rely on Authentication being present in the context.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
      }
      filterChain.doFilter(request,response);
    }
}
