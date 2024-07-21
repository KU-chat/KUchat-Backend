package kuchat.server.common.oauth;

import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

@Getter
public class CustomOAuth2User extends DefaultOAuth2User {
    private String email;
    private Role role;
    private Platform platform;
    private String providerId;

    @Builder
    public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                            Map<String, Object> attributes, String nameAttributeKey,
                            String email, Role role, Platform platform, String providerId) {
        super(authorities, attributes, nameAttributeKey);
        this.email = email;
        this.role = role;
        this.platform = platform;
        this.providerId = providerId;
    }
}
