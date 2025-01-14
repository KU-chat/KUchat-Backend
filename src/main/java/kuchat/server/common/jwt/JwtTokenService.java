package kuchat.server.common.jwt;


import io.jsonwebtoken.*;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.redis.RedisService;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Getter
@Slf4j
@RequiredArgsConstructor
@Service
public class JwtTokenService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;

    @Value("${secret.jwt.key}")
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
        String accessToken = getToken(memberId, claims, accessTokenExpiration);
        String refreshToken = getToken(memberId, claims, refreshTokenExpiration);
        redisService.setRefreshToken(memberId, refreshToken);
        return new AuthToken(accessToken, refreshToken);
    }

    private String getToken(Long memberId, Claims claims, long expiration) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(String.valueOf(memberId))       // subject : 토큰의 주체/사용자를 식별하기 위해 사용됨 -> "{provider}_{providerId}"
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private void isExpired(String token) {
        try {
            log.info("[isExpired] token: " + token);
            Jws<Claims> claims = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);
            if (claims.getBody().getExpiration().before(new Date())) {
                throw new KuchatException(EXPIRED_TOKEN);
            }
        } catch (ExpiredJwtException e) {
            throw new KuchatException(EXPIRED_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new KuchatException(UNSUPPORTED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new KuchatException(MALFORMED_TOKEN);
        } catch (SignatureException e) {
            throw new KuchatException(INVALID_SIGNATURE);
        } catch (JwtException e) {
            throw new KuchatException(INVALID_TOKEN);
        }
    }

    public boolean validatedRefreshToken(String refreshToken) {
        // 1. 토큰의 유효성 확인 : 리프레시 토큰이 올바르게 서명되었는지, 만료되지 않았는지 확인
        if (refreshToken == null) {
            return false;
        }
        String token = refreshToken.replaceAll("Bearer ", "");
        isExpired(token);

        // 2. redis에 저장된 최신 리프레시 토큰과 일치 여부 확인 -> 토큰 무효화 및 재발급에 중요
        Long memberId = getMemberId(token);
        String stored = redisService.getRefreshToken(memberId);
        return token.equals(stored);
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
        String token = refreshToken.replaceAll("Bearer ", "");
        Long memberId = getMemberId(token);
        Claims claims = getClaims(token);
        String role = claims.get("role", String.class);
        AuthToken authToken = generateAuthToken(Role.of(role), memberId);
        return authToken;
    }

    public String generateGuestToken(String platform, String providerId) {
        final Claims claims = Jwts.claims();        // claims = jwt token에 들어갈 정보, claim에 email을 넣어줘야 회원 식별 가능
        claims.put("platform", platform);
        claims.put("providerId", providerId);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public Member extractMemberByGuestToken(String guestToken) {
        log.info("[extractMemberByGuestToken] guestToken = {} ", guestToken);
        // 1. 토큰의 유효성 확인 : 리프레시 토큰이 올바르게 서명되었는지, 만료되지 않았는지 확인
        if (guestToken == null) {
            throw new KuchatException(NOT_FOUND_TOKEN);
        }
        String token = guestToken.replaceAll("Bearer ", "");
        isExpired(token);

        Claims body = getClaims(token);
        Platform platform = validatePlatform(body);
        String providerId = body.get("providerId", String.class);
        log.info("[extractMemberByGuestToken] member의 platform = {}, providerId = {}", platform, providerId);
        return memberRepository.findByPlatformAndProviderId(platform, providerId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    private Platform validatePlatform(Claims body) {
        Platform platform = Platform.of(body.get("platform", String.class));
        if (platform == null) {
            throw new KuchatException(MALFORMED_TOKEN);
        }
        return platform;
    }


    public Member extractMemberByAccessToken(String accessToken) {

        log.info("[extractMemberByAccessToken] accessToken = {} ", accessToken);

        if (accessToken == null) {
            throw new KuchatException(NOT_FOUND_TOKEN);
        }
        String token = accessToken.replaceAll("Bearer ", "");

        isExpired(token);
        Long memberId = getMemberId(token);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }
}
