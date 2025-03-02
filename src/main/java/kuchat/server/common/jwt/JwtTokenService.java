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
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Getter
@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class JwtTokenService {

    private final MemberRepository memberRepository;
    private final RedisService redisService;

    @Value("${secret.jwt.key}")
    private String secretKey;

    @Value("${secret.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${secret.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    private final static String ACCESS = "access";
    private final static String REFRESH = "refresh";
    private final static String GUEST = "guest";

    // GoogleOAuth2UserInfo의 email을 사용하여 token 발급
    public AuthToken generateAuthToken(Role role, Long memberId) {
        final Claims claims = Jwts.claims();        // claims = jwt token에 들어갈 정보, claim에 email을 넣어줘야 회원 식별 가능
        claims.put("role", role.getKey());
        claims.put("memberId", memberId);
        String accessToken = generateToken(claims, accessTokenExpiration, ACCESS);
        String refreshToken = generateToken(claims, refreshTokenExpiration, REFRESH);
        redisService.setRefreshToken(memberId, refreshToken);
        return new AuthToken(accessToken, refreshToken);
    }

    public String generateGuestToken(String platform, String providerId) {
        log.info("[generateGuestToken]");
        final Claims claims = Jwts.claims();        // claims = jwt token에 들어갈 정보, claim에 email을 넣어줘야 회원 식별 가능
        claims.put("platform", platform);
        claims.put("providerId", providerId);
        claims.put("type", GUEST);
        return generateToken(claims, accessTokenExpiration, GUEST);
    }

    private String generateToken(Claims claims, long expiration, String type) {
        claims.put("type", type);
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    private void isExpired(String token) throws JwtException {
        log.info("[isExpired] token: " + token);
        Jws<Claims> claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token);
        if (claims.getBody().getExpiration().before(new Date())) {
            log.info("토큰 만료 시간 = {}", claims.getBody().getExpiration());
            log.info("현재 시간 = {}", new Date());
            throw new KuchatException(EXPIRED_TOKEN);
        }
    }

    public Long validateRefreshToken(String refreshToken) {
        String token = validate(refreshToken, REFRESH);

        // redis에 저장된 최신 리프레시 토큰과 일치 여부 확인 -> 토큰 무효화 및 재발급에 중요
        Long memberId = getMemberId(token);
        String stored = redisService.getRefreshToken(memberId);
        if (token.equals(stored)) {
            return memberId;
        }
        throw new KuchatException(REFRESH_TOKEN_MISMATCH);
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    public Long getMemberId(String token) {
        Claims claims = getClaims(token);
        claims.get("type", String.class);
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .get("memberId", Long.class);
    }

    private Platform validatePlatform(Claims body) {
        Platform platform = Platform.of(body.get("platform", String.class));
        if (platform == null) {
            throw new KuchatException(MALFORMED_TOKEN);
        }
        return platform;
    }

    public Member extractMemberByGuestToken(String guestToken) {
        log.info("[extractMemberByGuestToken] guestToken = {} ", guestToken);
        String token = validate(guestToken, GUEST);
        Claims body = getClaims(token);
        Platform platform = validatePlatform(body);
        String providerId = body.get("providerId", String.class);
        log.info("[extractMemberByGuestToken] member의 platform = {}, providerId = {}", platform, providerId);
        return memberRepository.findByPlatformAndProviderId(platform.getValue(), providerId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    public Member extractMemberByAccessToken(String accessToken) {
        log.info("[extractMemberByAccessToken] accessToken = {} ", accessToken);
        String token = validate(accessToken, ACCESS);
        Long memberId = getMemberId(token);
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new KuchatException(NOT_FOUND_MEMBER));
    }

    private String validate(String rawToken, String type) {
        log.info("[validate] 토큰 = {}, 타입 = {}", rawToken, type);
        if (rawToken == null) {
            throw new KuchatException(NOT_FOUND_TOKEN);
        }
        String token = rawToken.replaceAll("Bearer ", "");
        isExpired(token);
        if (!validateTokenType(token, type)) {
            throw new KuchatException(TOKEN_TYPE_MISMATCH);
        }
        return token;
    }

    private boolean validateTokenType(String token, String type) {
        Claims claims = getClaims(token);
        String tokenType = claims.get("type", String.class);
        return tokenType.equals(type);
    }
}
