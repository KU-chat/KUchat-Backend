package kuchat.server.common.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class AuthToken {

    private String accessToken;
    private String refreshToken;

    public AuthToken(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
