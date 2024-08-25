package kuchat.server.domain.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import kuchat.server.common.exception.BaseResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.enums.MessageType;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Data
public class ChatMessage {

    @NotNull
    private Long chatroomId;

    @NotNull
    private String messageType;

    @NotNull
    private Long senderId;

    @NotBlank
    private String text;

    private Long parentId;

    @Builder
    public ChatMessage(Long chatroomId, MessageType messageType, Long senderId, String text) {
        this.chatroomId = chatroomId;
        this.messageType = messageType.toString();
        this.senderId = senderId;
        this.text = text;
        this.parentId = null;
    }

//    public ChatMessage(Message message) {
//        this.chatroomId = message.getMessageId();
//        this.messageType = message.getMessageType();
//        this.senderId = message.getSenderId();
//        this.text = message.getText();
//        this.parentId = ((message.getParent()) != null) ? message.getParent().getMessageId() : null;
//    }


//    public ChatMessage(ChatroomJoinRequest joinMessage) {
//        this.chatroomId = joinMessage.getChatroomId();
//        this.messageType = joinMessage.getMessageType();
//        this.senderId = joinMessage.getSenderId();
//        this.text = joinMessage.getText();
//        this.parentId = joinMessage.getParentId();
//    }

    public ChatMessage(String publishMessage) {
        // ChatMessage(chatroomId=1, messageType=JOIN, senderId=-1, text=이영선 님이 입장했습니다., parentId=null)

        try {
            Pattern pattern = Pattern.compile("\\(([^)]+)\\)");
            Matcher matcher = pattern.matcher(publishMessage);
            if (!matcher.find()) {
                throw new KuchatException(BaseResponse.NOT_FOUND_MESSAGE);
            }

            String message = matcher.group().replace("(", "").replace(")", "");       // 괄호안의 data
            log.info("message = {}", message);
            String[] strings = message.split(", ");
            for (String str : strings) {
                String[] map = str.split("=");
                switch (map[0]) {
                    case "chatroomId":
                        this.chatroomId = Long.parseLong(map[1]);
                        break;
                    case "messageType":
                        this.messageType = map[1];
                        break;
                    case "senderId":
                        this.senderId = Long.parseLong(map[1]);
                        break;
                    case "text":
                        this.text = map[1];
                        break;
                    case "parentId":
                        if (map[1].equals("null")) {
                            this.parentId = null;
                        } else {
                            this.parentId = Long.parseLong(map[1]);
                        }
                        break;
                }
            }
        } catch (Exception e) {
            throw new KuchatException(BaseResponse.CONVERT_TO_OBJECT_FAIL);
        }
    }
}
