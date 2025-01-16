package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.MemberInfoResponse;
import kuchat.server.domain.message.dto.MessageResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnterChatroomResponse extends BaseResponse {
    private String name;
    private List<MessageResponse> recentMessages;
    private List<MemberInfoResponse> memberInfos;

    public EnterChatroomResponse(String name) {
        this.name = name;
    }

    public void setMemberInfos(BaseResponseStatus responseStatus, List<Member> members) {
        this.responseStatus = responseStatus;
        List<MemberInfoResponse> memberInfos = members.stream()
                .map(MemberInfoResponse::new)
                .collect(Collectors.toList());
        this.memberInfos = memberInfos;
    }

}
