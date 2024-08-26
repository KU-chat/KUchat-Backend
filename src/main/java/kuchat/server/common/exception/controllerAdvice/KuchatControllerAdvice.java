package kuchat.server.common.exception.controllerAdvice;

import kuchat.server.common.response.ErrorResponse;
import kuchat.server.common.exception.JwtTokenException;
import kuchat.server.common.exception.KuchatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kuchat.server.common.response.BaseResponseStatus.INFO_BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class KuchatControllerAdvice {

    @ExceptionHandler(KuchatException.class)
    public ResponseEntity<ErrorResponse> handleKUchatException(KuchatException e){
        return new ResponseEntity(new ErrorResponse(e.getBaseResponseStatus()), e.getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
        return new ResponseEntity(new ErrorResponse(INFO_BAD_REQUEST), HttpStatus.BAD_REQUEST);
    }
}
