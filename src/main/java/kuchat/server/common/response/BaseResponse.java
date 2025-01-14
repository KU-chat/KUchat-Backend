package kuchat.server.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BaseResponse {
    protected BaseResponseStatus responseStatus;

    @JsonIgnore
    public HttpStatus getHttpStatus() {
        return responseStatus.getHttpStatus();
    }
}
