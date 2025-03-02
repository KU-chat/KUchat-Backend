package kuchat.server.common.oauth.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GoogleInfoResponse {
    private String id;
    private String email;
    private String name;
    private String picture;
}
