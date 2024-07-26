package kuchat.server.domain.enums;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "방문자"),
    STUDENT("ROLE_STUDENT", "학생"),
    ADMIN("ROLE_ADMIN", "관리자");

    private final String key;
    private final String value;

    public static Role of(String key) {
        for (Role role : Role.values()) {
            if (key.equals(role.key)) {
                return role;
            }
        }
        throw new KuchatException(BaseResponse.NOT_FOUND_ROLE);
    }
}
