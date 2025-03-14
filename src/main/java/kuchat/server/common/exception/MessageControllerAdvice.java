package kuchat.server.common.exception;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.MessageConversionException;
import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static kuchat.server.common.response.BaseResponseStatus.MESSAGE_FORMAT_ERROR;

@Slf4j
@RestControllerAdvice
public class MessageControllerAdvice {

    @MessageExceptionHandler(MessageConversionException.class)
    @SendToUser("/queue/errors")
    public BaseResponse handleMessageConversionException(MessageConversionException e) {
        log.error("[handleMessageConversionException] 에러 메시지 = {}", e.getMessage());
        return new BaseResponse(MESSAGE_FORMAT_ERROR);
    }
}
