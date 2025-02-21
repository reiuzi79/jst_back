package com.su.JobTracker.utils;
import io.jsonwebtoken.*;
import java.util.Date;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;

public class JwtUtil {
    private final String SECRET_KEY = "WPK593WV9XBEFM7B5NJT86XT49RFV79VGGYR5D5NY57C03JMWVWUPJG58V09P0QCDJNRCUJN3D98R0BXBUR5H3K32UUW9GJE9ARB8FVJLPPGBCJKPVASRNRC5L1DMQQWX6AZAXTSH5634VTTA7XJVSSUY21KVVK0SLSR8WXYABRVKNVJ7MX69AYDY7WZU131E6R593BQ3E7V31RWMWH5B360Y9LCBN7UYW38ZAPJCU9JTS0WBXAVQK282XWPJCSS";
    Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    
    private Key getSigningKey() {
        return new SecretKeySpec(SECRET_KEY.getBytes(), "HmacSHA256");
    }
    
    public String generateToken(String email, String role) {
        Key key = new SecretKeySpec(SECRET_KEY.getBytes(), SignatureAlgorithm.HS256.getJcaName());
        return Jwts.builder()
                .setSubject(email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 600)) // 600 mins
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
    
    public boolean isTokenValid(String token, String email) {
        return extractEmail(token).equals(email) && !isTokenExpired(token);
    }

    public boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return expiration.before(new Date());
    }
    
    public boolean isAdmin(String token) {
        return "admin".equals(extractRole(token));
    }

    public boolean isApplicant(String token) {
        return "applicant".equals(extractRole(token));
    }

    public boolean isCompanyRepresentative(String token) {
        return "company_representative".equals(extractRole(token));
    }
}
