package kuchat.server.domain.chat;

import jakarta.persistence.*;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.BaseTime;
import kuchat.server.domain.enums.ChatState;
import kuchat.server.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static kuchat.server.domain.enums.ChatState.ACTIVE;
import static kuchat.server.domain.enums.ChatState.CLOSED;

@Getter
@Entity
@NoArgsConstructor
public class Chat extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_id")
    private Long id;

    // 양방향 연관 관계(+ 연관관계 편의 메서드)는 필요할 때 만들기
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ChatMember> chatMembers = new ArrayList<>();

    private String name;

    private String image;

    @Enumerated(value = EnumType.STRING)
    private ChatState state;

    public void addChatMember(ChatMember chatMember) {
        if(!chatMembers.contains(chatMember)){
            chatMembers.add(chatMember);
        }
    }

    public Chat(String name, String image) {
        this.name = name;
        this.image = image;
        this.state = ACTIVE;
    }

    public void close() {
        state = CLOSED;
    }

    public void open() {
        state = ACTIVE;
    }

    public boolean isActive() {
        return state == ACTIVE;
    }

    public boolean isGroup() {
        return chatMembers.size() > 2;
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", image='" + image + '\'' +
                ", state=" + state +
                '}';
    }
}
