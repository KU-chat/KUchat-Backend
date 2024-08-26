package kuchat.server.common.exception.controllerAdvice;

import kuchat.server.common.response.ErrorResponse;
import kuchat.server.common.exception.JwtTokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kuchat.server.common.response.BaseResponseStatus.INFO_BAD_REQUEST;
import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_TOKEN;

@Slf4j
@RestControllerAdvice
public class JwtControllerAdvice {

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenException(JwtTokenException e) {
        return new ResponseEntity(new ErrorResponse(e.getBaseResponseStatus()), e.getHttpStatus());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingHeaderException(MissingRequestHeaderException e) {
        return new ResponseEntity(new ErrorResponse(NOT_FOUND_TOKEN), HttpStatus.NOT_FOUND);
    }
}
