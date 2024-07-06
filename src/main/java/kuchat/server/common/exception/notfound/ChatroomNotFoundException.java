package kuchat.server.common.exception.notfound;

public class ChatroomNotFoundException extends NotFoundException{
    public ChatroomNotFoundException() {
        super("존재하지 않는 채팅방입니다.", 4001);
    }
}
