package com.metrocal.metrocal.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.metrocal.metrocal.entities.User;

import java.util.function.Function;




import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {
    
     private final String SECRET_KEY = "95afa3d2de887b31b68241611080b7cc19753db14c8ec183805d5a32485a76d5";

    public String generateToken(User user){

        Map<String, Object> claims = new HashMap<>();
    claims.put("role", user.getUserRole().name()); // ajoute le rôle en tant que String ("CLIENT")

        String token = Jwts
              .builder()
              .subject(user.getUsername())
              .issuedAt(new Date(System.currentTimeMillis()))
              .expiration(new Date(System.currentTimeMillis() + 24*60*60*1000))
              .signWith(getSigningKey())
              .compact();

          return token;    
    }

   

    private Claims extractAllClaims(String token) {
        return Jwts
               .parser()
               .verifyWith(getSigningKey())
               .build()
               .parseSignedClaims(token)
               .getPayload();
    }

    private SecretKey getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }


    
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
     }

   public boolean isValid(String token, UserDetails user) {
    String username = extractUsername(token);
    boolean expired = isTokenExpired(token);
    System.out.println("Token username: " + username);
    System.out.println("UserDetails username: " + user.getUsername());
    System.out.println("Token expired? " + expired);
    return (username.equals(user.getUsername())) && !expired;
}


    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

  
}
