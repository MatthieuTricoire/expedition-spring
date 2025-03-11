package org.wcs.myblog.service;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {
  @Value("${security.jwt.secret-key}")
  private String secretKey;

  @Value("${security.jwt.expiration-time}")
  private long jwtExpiration;

  public String generateToken(UserDetails userDetails) {
    SecretKey key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .claim("roles", userDetails.getAuthorities())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
        // .signWith(SignatureAlgorithm.HS256, secretKey) deprecated method
        .signWith(key)
        .compact();
  }

  public Claims extractClaims(String token) {
    SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    // Deprecated
    // return Jwts.parser()
    // .setSigningKey(secretKey)
    // .parseClaimsJws(token)
    // .getBody();
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody();

  }

  public boolean validateJwtToken(String token) {
    try {
      Jwts.parserBuilder().setSigningKey(secretKey.getBytes()).build().parseClaimsJws(token);
      return true;
    } catch (JwtException | IllegalArgumentException e) {
      return false;
    }
  }
}
