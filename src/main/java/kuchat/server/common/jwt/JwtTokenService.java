package kuchat.server.common.jwt;


import io.jsonwebtoken.*;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static kuchat.server.common.exception.BaseResponse.*;

@Getter
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final MemberRepository memberRepository;

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

        return AuthToken.of(accessToken, refreshToken, accessTokenExpiration, refreshTokenExpiration);
    }

    public boolean isExpired(String token) {

        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(token);
            return claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (UnsupportedJwtException e) {
            throw new KuchatException(UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new KuchatException(MALFORMED_TOKEN);
        } catch (SignatureException e) {
            throw new KuchatException(INVALID_SIGNATURE);
        } catch (JwtException e) {
            log.error("[isExpired] jwt 토큰 오류 = {}", e.getMessage());
            throw new KuchatException(INVALID_TOKEN);
        }
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
}