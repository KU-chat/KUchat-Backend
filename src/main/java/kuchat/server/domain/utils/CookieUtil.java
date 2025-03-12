package kuchat.server.domain.utils;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.domain.auth.dto.AuthTokenResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;

import static kuchat.server.common.response.BaseResponseStatus.SUCCESS;

public class CookieUtil {

    private static final int REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60;   // 7일
    private static final int ACCESS_TOKEN_EXPIRATION = 30 * 60;             // 30분
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String ACCESS_TOKEN = "accessToken";

    public static ResponseEntity<BaseResponse> setAuthToken(AuthTokenResponse response) {
        HttpHeaders headers = new HttpHeaders();
        ResponseCookie refreshTokenCookie = createCookie(
                REFRESH_TOKEN, response.getRefreshToken(), REFRESH_TOKEN_EXPIRATION);
        ResponseCookie accessTokenCookie = createCookie(
                ACCESS_TOKEN, response.getAccessToken(), ACCESS_TOKEN_EXPIRATION);

        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());

        return ResponseEntity.ok()
                .headers(headers)
                .body(new BaseResponse(SUCCESS));
    }

    public static ResponseEntity<BaseResponse> removeAuthToken() {
        ResponseCookie noAccessTokenCookie = createCookie(ACCESS_TOKEN, "", 0);
        ResponseCookie noRefreshTokenCookie = createCookie(REFRESH_TOKEN, "", 0);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, noAccessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, noRefreshTokenCookie.toString());
        return ResponseEntity.ok()
                .headers(headers)
                .body(new BaseResponse(SUCCESS));
    }

    private static ResponseCookie createCookie(String key, String value, int age) {
        return ResponseCookie.from(key, value)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(age)
                .sameSite("None")
                .build();
    }
}
