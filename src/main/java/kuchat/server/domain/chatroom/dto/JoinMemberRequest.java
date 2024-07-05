package kuchat.server.domain.chatroom.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class JoinMemberRequest {
    private List<Long> joinMembers;
}
