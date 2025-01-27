package kuchat.server.common.exception;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import kuchat.server.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RestControllerAdvice
public class JwtControllerAdvice {

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponse> handleExpiredJwtException(ExpiredJwtException e) {
        log.error("[handleExpiredJwtException] 만료된 jwt 토큰입니다. 다시 로그인해주세요.");
        HttpStatus httpStatus = EXPIRED_TOKEN.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(EXPIRED_TOKEN));
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<BaseResponse> handleMalformedJwtException(MalformedJwtException e) {
        log.error("[handleMalformedJwtException] 올바르게 구성되지 않은 jwt 토큰입니다.");
        HttpStatus httpStatus = MALFORMED_TOKEN.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(MALFORMED_TOKEN));
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<BaseResponse> handleJwtException(JwtException e) {
        log.error("[handleJwtException] jwt 토큰 오류입니다.");
        HttpStatus httpStatus = INVALID_TOKEN.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(INVALID_TOKEN));
    }
}
