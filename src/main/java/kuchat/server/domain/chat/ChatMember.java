package kuchat.server.domain.chat;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.member.Member;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Entity
@Table(name = "chat_member")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//@SequenceGenerator (
//        name = "CHAT_MEMBER_SEQ_GENERATOR" ,
//        sequenceName ="CHAT_MEMBER_SEQ",       //매핑할 데이터베이스 시퀀스 이름
//        initialValue = 1, allocationSize = 100)
public class ChatMember extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CHAT_MEMBER_SEQ_GENERATOR")
    @Column(name = "chat_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    //== 생성 메서드 (연관관계 편의 메서드) ==//
    // ChatMember 생성 : ChatMember 생성자만 호출하면, Chat 엔티티의 메서드를 따로 호출할 필요는 없다.
    // ChatMember 삭제 : ChatMember 엔티티가 삭제되면 Chat에 있던 ChatMember도 같이 사라진다.
    public ChatMember(Chat chat, Member member) {
        this.chat = chat;
        this.member = member;
        chat.addChatMember(this);       // chat - chatMember 만 양방향 관계니까 이 관계에 대해서만 추가하면 됨
    }

    public boolean matches(Chat chat, Member member) {
        return this.chat.equals(chat) && this.member.equals(member);
    }
}
