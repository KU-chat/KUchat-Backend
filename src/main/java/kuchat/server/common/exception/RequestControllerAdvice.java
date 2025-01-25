package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RestControllerAdvice
public class RequestControllerAdvice {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse> handleMissingParameterException(MissingServletRequestParameterException e) {
        log.error("[handleMissingParameterException] 클라이언트 요청에서 request parameter가 누락된 경우");
        HttpStatus httpStatus = PARAMETER_NOT_FOUND.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(PARAMETER_NOT_FOUND));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse> handleUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("[handleUploadSizeExceededException] 파일의 용량이 초과된 경우");
        HttpStatus httpStatus = OVER_SIZE_IMAGE.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(OVER_SIZE_IMAGE));
    }

    @ExceptionHandler(MultipartException.class)
    public ResponseEntity<BaseResponse> handleMissingMultipartException(MultipartException e) {
        log.error("[handleMissingMultipartException] 파일의 용량이 초과된 경우");
        HttpStatus httpStatus = NOT_FOUND_IMAGE.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(NOT_FOUND_IMAGE));
    }
}
