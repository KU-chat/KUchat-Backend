package kuchat.server.domain.jwt;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

import static kuchat.server.common.exception.BaseResponse.MALFORMED_TOKEN;
import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_MEMBER;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenService {

    private static final String ACCESS_TOKEN_SUBJECT = "access_token";
    private static final String REFRESH_TOKEN_SUBJECT = "refresh_token";
    private final MemberRepository memberRepository;

    @Value("${secret.jwt.secret-key}")
    private String secretKey;

    @Value("${secret.jwt.access.expiration}")
    private long accessTokenExpiration;

    @Value("${secret.jwt.refresh.expiration}")
    private long refreshTokenExpiration;

    @Value("${secret.jwt.access.header}")
    private String accessHeader;

    @Value("${secret.jwt.refresh.header}")
    private String refreshHeader;


    // GoogleOAuth2UserInfo의 email을 사용하여 token 발급
    public String generateAccessToken(String email) {

        final Claims claims = Jwts.claims();        // claims = jwt token에 들어갈 정보, claim에 email을 넣어줘야 회원 식별 가능
        claims.put("email", email);

        log.info("[generateAccessToken] secretKey: " + secretKey);
        log.info("[generateAccessToken] email: " + email);
        log.info("[generateAccessToken] refreshTokenExpiration: " + refreshTokenExpiration);


        return Jwts.builder()
                .setClaims(claims)
                .setSubject(ACCESS_TOKEN_SUBJECT)       // subject : 토큰의 주체/사용자를 식별하기 위해 사용됨
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public String generateRefreshToken() {
        return Jwts.builder()
                .setSubject(REFRESH_TOKEN_SUBJECT)       // subject : 토큰의 주체/사용자를 식별하기 위해 사용됨
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // response header에 access_token 실어서 보내기
    public void sendAccessToken(HttpServletResponse response, String accessToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        log.info("재발급된 access token : {}", accessToken);
    }

    // response header에 access_token과 refresh_token 실어서 보내기
    public void sendAccessAndRefreshToken(HttpServletResponse response, String accessToken, String refreshToken) {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setHeader(accessHeader, accessToken);
        response.setHeader(refreshHeader, refreshToken);
        log.info("[sendAccessAndRefreshToken] 재발급된 access token : {} , refresh token : {}", accessToken, refreshToken);
        log.info("[sendAccessAndRefreshToken] access token, refresh token 헤더에 추가 완료");
    }


    public Optional<String> extractAccessToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(accessHeader))
                .filter(str -> str.startsWith("Bearer "))
                .map(accessToken -> accessToken.replace("Bearer ", ""));
    }

    public Optional<String> extractRefreshToken(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader(refreshHeader))
                .filter(str -> str.startsWith("Bearer "))
                .map(refreshToken -> refreshToken.replace("Bearer ", ""));
    }

    // 토큰에서 멤버의 email 추출
    public String extractEmail(String token) {
        try {
            return getBody(token)
                    .get("email")
                    .toString();
        } catch (Exception e) {
            throw new KuchatException(MALFORMED_TOKEN);
        }
    }

    // 토큰의 만료시간이 지났는지 확인
    public boolean isExpired(String token) {
        Date expiredDate = getBody(token).getExpiration();
        return expiredDate.before(new Date());
    }

    private Claims getBody(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJwt(token)
                .getBody();
    }

    public String getAccessHeader() {
        return accessHeader;
    }

    public String getRefreshHeader() {
        return refreshHeader;
    }

    @Transactional
    public void updateRefreshToken(String email, String refreshToken) {

        memberRepository.findByEmail(email)
                .ifPresentOrElse(
                        member -> member.updateRefreshToken(refreshToken),
                        () -> new KuchatException(NOT_FOUND_MEMBER)
                );
        log.info("[updateRefreshToken] refresh token 업데이트 완료!");
    }
}