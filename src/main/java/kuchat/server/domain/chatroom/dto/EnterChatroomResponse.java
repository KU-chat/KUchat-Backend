package kuchat.server.domain.chatroom.dto;

import kuchat.server.common.exception.BaseResponse;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.dto.MemberInfoResponse;
import kuchat.server.domain.message.Message;
import kuchat.server.domain.message.dto.MessageResponse;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class EnterChatroomResponse {
    private String name;
    private List<MessageResponse> recentMessages;
    private List<MemberInfoResponse> memberInfos;
    @Setter
    private BaseResponse baseResponse;

    public EnterChatroomResponse(String name){
        this.name = name;
    }

    public void setMemberInfos(List<Member> members) {
        List<MemberInfoResponse> memberInfos = members.stream()
                .map(MemberInfoResponse::new)
                .collect(Collectors.toList());
        this.memberInfos = memberInfos;
    }

//    public void setRecentMessages(List<Message> messages) {
//        List<MessageResponse> messageResponses = messages.stream()
//                .map(MessageResponse::new)
//                .collect(Collectors.toList());
//        this.recentMessages = messageResponses;
//    }
}
