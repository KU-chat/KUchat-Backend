package kuchat.server.common.jwt;

import kuchat.server.domain.enums.Role;
import kuchat.server.domain.member.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private Member member;

    public CustomUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.stream(Role.values()).toList().stream()
                .map(role -> new SimpleGrantedAuthority(role.getKey()))
                .collect(Collectors.toList());
        // new SimpleGrantedAuthority("ROLE_GUEST"), new SimpleGrantedAuthority("ROLE_STUDENT")
        // role 은 위와 같이 변환되어 spring security 에서 인증된 사용자의 권한을 확인하는데 사용된다.
    }

    @Override
    public String getPassword() {
        return null;        // 소셜로그인으로 구현했기 때문에 우리 서버에서 직접 password 에 접근할 수 없다.
    }

    @Override
    public String getUsername() {
        return member.getName();
    }

    // 소셜로그인으로 구현했기에 아래 메서드의 리턴값을 모두 true로 해준다.
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
