package org.example.Helper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.Configuration.EnvConfig;

import java.util.Base64;

public class JwtHelper {
    private static String secretKey = EnvConfig.getSecretKey();
    public static Claims ExtractTokenBody(String token){
        return Jwts.parser()
                .setSigningKey(Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey)))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public static String extractUsername(String token) {
        Claims claims = ExtractTokenBody(token);
        return claims.get("username", String.class);// "sub" claim
    }

    public static boolean IsValidToken(String token){
        try {
            Claims claims = ExtractTokenBody(token);
            return true;
        }catch(Exception e) {
            return false;
        }
    }
}
