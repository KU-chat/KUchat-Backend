package kuchat.server.domain.oauth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleInfoResponse {
    @JsonProperty("sub")
    private String id;
    private String email;
    private String name;
    private String picture;
}
