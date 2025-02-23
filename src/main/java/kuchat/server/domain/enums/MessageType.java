package kuchat.server.domain.enums;


public enum MessageType {
    ENTER, TALK, LEAVE;
    // REPLY, TRANSLATE;     // 사용자가 보내는 메세지

    public static MessageType fromString(String name) {
        try {
            return MessageType.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return MessageType.TALK;
        }
    }
}
