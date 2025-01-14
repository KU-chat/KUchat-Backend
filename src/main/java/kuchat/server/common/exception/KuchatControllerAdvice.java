package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static kuchat.server.common.response.BaseResponseStatus.DATE_BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class KuchatControllerAdvice {

    @ExceptionHandler(KuchatException.class)
    public ResponseEntity<BaseResponse> handleKuchatException(KuchatException e) {
        log.error(String.valueOf(e.getResponse()));
        HttpStatus httpStatus = e.getResponse().getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(e.getResponse());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<BaseResponse> handleDateTimeParseException() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new BaseResponse(DATE_BAD_REQUEST));
    }
}
