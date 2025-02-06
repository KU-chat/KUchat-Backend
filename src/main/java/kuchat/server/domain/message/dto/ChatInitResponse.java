package kuchat.server.domain.message.dto;

import java.util.List;

public class ChatInitResponse {
    private String comment;
    private List<String> users;

    public ChatInitResponse(String comment, List<String> users) {
        this.comment = comment;
        this.users = users;
    }
}
