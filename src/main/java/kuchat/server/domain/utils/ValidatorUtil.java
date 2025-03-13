package kuchat.server.domain.utils;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.Error;
import kuchat.server.common.response.ErrorResponse;
import org.springframework.validation.BindingResult;

import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.INFO_BAD_REQUEST;

public class ValidatorUtil {

    public static void validateRequest(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<Error> errors = bindingResult.getFieldErrors().stream()
                    .map(error -> new Error(error.getField(), error.getDefaultMessage()))
                    .toList();
            ErrorResponse errorResponse = new ErrorResponse(INFO_BAD_REQUEST, errors);
            throw new KuchatException(errorResponse);
        }
    }

    public static int sizeValidator(int size){
        if (size > 100){
            return 50;
        }
        return size;
    }
}
