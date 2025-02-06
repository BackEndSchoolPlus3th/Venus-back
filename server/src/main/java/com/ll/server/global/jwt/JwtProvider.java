package com.ll.server.global.jwt;

import com.ll.server.domain.member.entity.Member;
import com.ll.server.global.jpa.util.Ut;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtProvider {

    @Value("${spring.oauth2.jwt.secret-key}")
    private String secretKeyOrigin;

    @Value("${spring.oauth2.accessToken.expirationSeconds}")
    private int accessTokenExpirationSeconds;

    // 변수선언
    private static SecretKey cachedSecretKey;




    public SecretKey getSecretKey() {
        if (cachedSecretKey == null) {
            cachedSecretKey = _getSecretKey();
        }
        return cachedSecretKey;
    }
    private SecretKey _getSecretKey() {
        String keyBase64Encoded = Base64.getEncoder().encodeToString(secretKeyOrigin.getBytes());
        return Keys.hmacShaKeyFor(keyBase64Encoded.getBytes());
    }
    public String genAccessToken(Member member) {
        return genToken(member, accessTokenExpirationSeconds);
    }

    public String genRefreshToken(Member member) {
        // 1년동안 유효
        return genToken(member, 60 * 60 * 24 * 365 * 1);
    }

    public String genToken(Member member, int seconds) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("username", member.getNickname());
        claims.put("email", member.getEmail());
        long now = new Date().getTime();
        Date accessTokenExpiresIn = new Date(now + 1000L * seconds);
        return Jwts.builder()
                .claim("body", Ut.json.toStr(claims))
                .setExpiration(accessTokenExpiresIn)
                .signWith(getSecretKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public Map<String, Object> getClaims(String accessToken) {

        String body = Jwts.parserBuilder()
                .setSigningKey(getSecretKey())
                .build()
                .parseClaimsJws(accessToken)
                .getBody()
                .get("body", String.class);
        return Ut.toMap(body);
    }

    // 유효성 검증
    public boolean verify (String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // JWT 토큰에서 사용자 ID 추출
    public static Long getUserIdFromToken(String accessToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(cachedSecretKey)
                .parseClaimsJws(accessToken)
                .getBody();
        return Long.valueOf(claims.get("id", String.class));
    }



}
