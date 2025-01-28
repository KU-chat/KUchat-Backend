package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;

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
        log.error(e.getMessage());
        HttpStatus httpStatus = NOT_FOUND_IMAGE.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(NOT_FOUND_IMAGE));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<BaseResponse> handleMissingPathVariableException(HttpRequestMethodNotSupportedException e){
        log.error("[handleMissingPathVariableException] 요청 url에서 path variable이 누락된 경우");
        log.error(e.getMessage());
        log.error(String.valueOf(e.getHeaders()));
        HttpStatus httpStatus = PATH_VARIABLE_NOT_FOUND.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(PATH_VARIABLE_NOT_FOUND));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<BaseResponse> handlerNotFoundException(NoHandlerFoundException e){
        log.error("[handlerNotFoundException] 구현되지 않은 API로 요청을 보낸 경우");
        log.error(e.getMessage());
        HttpStatus httpStatus = API_NOT_FOUND.getHttpStatus();
        return ResponseEntity.status(httpStatus)
                .body(new BaseResponse(API_NOT_FOUND));
    }
}
