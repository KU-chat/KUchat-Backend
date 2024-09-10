package kuchat.server.domain.enums;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Status {
    ACTIVE("ACTIVE", "활성"),
    WITHDRAWN("WITHDRAWN", "탈퇴"),
    PENDING("PENDING", "대기"),           // 구글 소셜 로그인은 했지만 회원가입 처리가 완료되지 않은 회원 (ROLE_GUEST)
    SUSPENDED("SUSPENDED", "정지");       // 규칙 위반 등으로 임시 정지된 회원

    private final String key;
    private final String value;

//    public static Status of(String key) {
//        for (Status status : Status.values()) {
//            if (key.equals(status.key)) {
//                return status;
//            }
//        }
//        throw new KuchatException(BaseResponseStatus.NOT_FOUND_ROLE);
//    }
}
