package kuchat.server.common.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleInfoResponse {
    private String id;
    private String email;
    private String name;
    private String picture;
}
