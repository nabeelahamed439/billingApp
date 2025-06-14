package com.example.demo.config;
import com.example.demo.entity.User;
import com.example.demo.auth.TokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    private static final String PRIVATE_KEY ="62FEBDF7EABA589751DE21271E3AD62FEBDF7EABA589751DE21271E3AD";


    public String extractUserName(String token)  {
        return extractClaim(token,Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String,Object> claims = claimsSetting(userDetails);
        return generateToken(claims,userDetails);
    }

    private Map<String,Object> claimsSetting(UserDetails userDetails){
        User user = (User) userDetails;
        Map<String,Object> claims = new HashMap<>();
        claims.put("userId", user.getUserId());
        claims.put("firstName", user.getFirstName());
        claims.put("lastName", user.getLastName());
        claims.put("email", user.getEmail());
        claims.put("phoneNumber", user.getPhoneNumber());
        claims.put("authorities", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return claims;
    }

    public String generateToken(Map<String, Object> claims, UserDetails userDetails) {
        long oneWeekMillis = 1000 * 60 * 60 * 24 * 7;
        return Jwts
                .builder() 
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + oneWeekMillis))
                .signWith(getPrivateKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    public boolean isTokenValid(String token,UserDetails userDetails){
        final String username = extractUserName(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }
    private boolean isTokenExpired(String token)  {
        return extractExpiration(token).before(new Date());
    }
    private Date extractExpiration(String token) {
        return extractClaim(token,Claims::getExpiration);
    }
    private Claims extractAllClaims(String token) {
        Claims claims = Jwts
                .parserBuilder()
                .setSigningKey(getPrivateKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        TokenData.setUserId(claims.get("userId", String.class));
        TokenData.setFirstName(claims.get("firstName", String.class));
        TokenData.setLastName(claims.get("lastName", String.class));
        TokenData.setEmail(claims.get("email", String.class));
        TokenData.setPhoneNumber(claims.get("phoneNumber", String.class));

        // Handle authorities (now stored in "authorities" claim)
        List<String> authorities = claims.get("authorities", List.class);
        if (authorities != null && !authorities.isEmpty()) {
            TokenData.setUserRole(authorities.getFirst().replace("ROLE_", ""));
        }
        return claims;
    }


    private Key getPrivateKey(){
        byte[] key = Decoders.BASE64.decode(PRIVATE_KEY);
        return Keys.hmacShaKeyFor(key);
    }

}
