package kuchat.server.domain.enums;

public enum FriendType {
    PENDING,            // 보낸 친구 신청이 수락/거절되지 않음 (사용자가 보류중)
    FRIEND                 // 쌍방친구관계
//    BLOCKED,                // 내가 차단한 친구
//    BLOCKED_BY;             // 내가 그 친구에게 차단 당함
}
