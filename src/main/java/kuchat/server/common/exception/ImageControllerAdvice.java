package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_IMAGE;
import static kuchat.server.common.response.BaseResponseStatus.OVER_SIZE_IMAGE;

@Slf4j
@RestControllerAdvice
public class ImageControllerAdvice {
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponse> handleMissingImageException(MissingServletRequestParameterException e) {
        // 클라이언트에서 multipartFile 필드 이름을 잘못 지정했거나, 파일 자체를 전송하지 않은 경우

        log.error("[handleMissingImageException] 클라이언트 요청에서 multipartFile이 누락된 경우");
        HttpStatus httpStatus = NOT_FOUND_IMAGE.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(NOT_FOUND_IMAGE));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<BaseResponse> handleUploadSizeExceededException(MaxUploadSizeExceededException e) {
        log.error("[handleUploadSizeExceededException] 파일의 용량이 초과된 경우");
        HttpStatus httpStatus = OVER_SIZE_IMAGE.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(OVER_SIZE_IMAGE));
    }
}
