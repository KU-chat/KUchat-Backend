package kuchat.server.domain.chatroom;

import jakarta.persistence.*;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.Status;
import lombok.*;

@Entity
@ToString
@Table(name = "chatroom")
@Getter
@NoArgsConstructor
public class Chatroom extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chatroom_id")
    private Long id;

    @Setter
    private String name;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

//    @OneToMany(mappedBy = "chatroom", cascade = CascadeType.ALL, orphanRemoval = true)
//    private Set<ChatroomMember> chatroomMembers = new HashSet<>();       // 채팅방에 속한 클라이언트들 리스트


    @Builder
    public Chatroom(String name) {
        this.name = name;
        status = Status.ACTIVE;
    }

    /**
     * ChatroomMembers HashSet에서 ChatroomMember 객체를 제거한 뒤, hashset의 size를 반환
     */
//    public int deleteMember(ChatroomMember chatroomMember) {
//        if (chatroomMembers.remove(chatroomMember)) {
//            return chatroomMembers.size();
//        } else {
//            throw new KuchatException(BaseResponseStatus.EXIT_CHATROOM_FAIL);
//        }
//    }
//
//    public void addMember(ChatroomMember chatroomMember) {
//        chatroomMembers.add(chatroomMember);
//    }
}
