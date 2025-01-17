package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.response.BaseResponse;
import kuchat.server.common.response.BaseResponseStatus;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.ProfileResponse;
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
    private List<ProfileResponse> memberProfiles;

    public EnterChatroomResponse(String name) {
        this.name = name;
    }

    public void setMemberInfos(BaseResponseStatus responseStatus, List<Member> members) {
        this.responseStatus = responseStatus;
        this.memberProfiles = members.stream()
                .map(member -> new ProfileResponse(member.getProfile()))
                .collect(Collectors.toList());
    }

}
