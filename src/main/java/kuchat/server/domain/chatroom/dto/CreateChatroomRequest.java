package kuchat.server.domain.chatroom.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CreateChatroomRequest {

    @NotBlank
    private String name;

    @NotNull
    private List<Long> memberIds;
}