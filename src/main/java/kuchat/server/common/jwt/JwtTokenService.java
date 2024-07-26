package kuchat.server.common.jwt;


import io.jsonwebtoken.*;
import kuchat.server.common.exception.JwtTokenException;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.common.redis.RedisService;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Date;

import static kuchat.server.common.exception.BaseResponse.*;

@Getter
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;

    @Value("${secret.jwt.secret-key}")
    private String secretKey;

    @Value("${secret.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${secret.jwt.refresh.expiration}")
    private long refreshTokenExpiration;


    // GoogleOAuth2UserInfo의 email을 사용하여 token 발급
    public AuthToken generateAuthToken(Role role, Long memberId) {
        final Claims claims = Jwts.claims();        // claims = jwt token에 들어갈 정보, claim에 email을 넣어줘야 회원 식별 가능
        claims.put("role", role.getKey());
        claims.put("memberId", memberId);

        log.info("[generateAuthToken] secretKey: " + secretKey);
        log.info("[generateAuthToken] memberId: " + memberId);

        String accessToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(memberId))       // subject : 토큰의 주체/사용자를 식별하기 위해 사용됨 -> "{provider}_{providerId}"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        String refreshToken = Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(memberId))       // subject : 토큰의 주체/사용자를 식별하기 위해 사용됨 -> "{provider}_{providerId}"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        redisService.setRefreshToken(memberId, refreshToken);

        return AuthToken.of(accessToken, refreshToken, accessTokenExpiration, refreshTokenExpiration);
    }

    private boolean isExpired(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (UnsupportedJwtException e) {
            throw new JwtTokenException(UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new JwtTokenException(MALFORMED_TOKEN);
        } catch (SignatureException e) {
            throw new JwtTokenException(INVALID_SIGNATURE);
        } catch (JwtException e) {
            log.error("[isExpired] jwt 토큰 오류 = {}", e.getMessage());
            throw new JwtTokenException(INVALID_TOKEN);
        }
    }

    public boolean validatedRefreshToken(String refreshToken) {
        // 1. 토큰의 유효성 확인 : 리프레시 토큰이 올바르게 서명되었는지, 만료되지 않았는지 확인
        if (refreshToken == null){
            return false;
        } if(isExpired(refreshToken)) {
            return false;
        }

        // 2. redis에 저장된 최신 리프레시 토큰과 일치 여부 확인 -> 토큰 무효화 및 재발급에 중요
        Long memberId = getMemberId(refreshToken);
        String stored = redisService.getRefreshToken(memberId);
        return refreshToken.equals(stored);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getMemberId(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", Long.class);
    }

    public AuthToken reissue(String refreshToken) {
        Long memberId = getMemberId(refreshToken);
        Claims claims = getClaims(refreshToken);
        String role = claims.get("role", String.class);
        return generateAuthToken(Role.of(role), memberId);
    }

}