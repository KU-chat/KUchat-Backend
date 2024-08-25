package kuchat.server.common.exception.controllerAdvice;

import io.jsonwebtoken.JwtException;
import kuchat.server.common.exception.ErrorResponse;
import kuchat.server.common.exception.JwtTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_TOKEN;

@Slf4j
@RestControllerAdvice
public class JwtControllerAdvice {

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleJwtException(JwtTokenException e) {
        return new ResponseEntity(new ErrorResponse(e.getCode(), e.getMessage()), e.getHttpStatus());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeaderException(MissingRequestHeaderException e) {
        return new ResponseEntity(new ErrorResponse(NOT_FOUND_TOKEN), HttpStatus.NOT_FOUND);
    }
}
