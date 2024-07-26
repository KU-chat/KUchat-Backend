package kuchat.server.common.jwt;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthToken {

    private String accessToken;
    private String refreshToken;
    private Long AccessTokenExpiresIn;
    private Long refreshTokenExpiresIn;

    public static AuthToken of(String accessToken, String refreshToken, Long AccessTokenExpiresIn, Long refreshTokenExpiresIn) {
        return new AuthToken(accessToken, refreshToken, AccessTokenExpiresIn, refreshTokenExpiresIn);
    }
}
