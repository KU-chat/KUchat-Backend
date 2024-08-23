package kuchat.server.common.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kuchat.server.common.exception.BaseResponse.INFO_BAD_REQUEST;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
public class KuchatExceptionHandler {
    @ExceptionHandler(KuchatException.class)
    public ResponseEntity<ErrorResponse> handleKUchatException(KuchatException e){
        return ResponseEntity.status(e.getHttpStatus()).body(new ErrorResponse(e.getCode(), e.getMessage()));
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e){
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(new ErrorResponse(INFO_BAD_REQUEST.getCode(), e.getMessage()));
//    }

    @ExceptionHandler(JwtTokenException.class)
    public ResponseEntity<ErrorResponse> handleJwtTokenException(JwtTokenException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(INFO_BAD_REQUEST.getCode(), e.getMessage()));
    }
}
