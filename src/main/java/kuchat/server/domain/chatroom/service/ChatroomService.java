package kuchat.server.domain.chatroom.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.notfound.NotFoundMemberException;
import kuchat.server.common.socket.WebSocketHandler;
import kuchat.server.domain.chatroom.Chatroom;
import kuchat.server.domain.chatroom.dto.ChatroomResponse;
import kuchat.server.domain.chatroom.dto.CreateChatroomRequest;
import kuchat.server.domain.chatroom.dto.FindChatroomResponse;
import kuchat.server.domain.chatroom.dto.FindChatroomsResponse;
import kuchat.server.domain.chatroom.repository.ChatroomRepository;
import kuchat.server.domain.enums.MessageType;
import kuchat.server.domain.message.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.WebSocketSession;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final WebSocketHandler webSocketHandler;
    private final ObjectMapper objectMapper;

    public void handlerActions(WebSocketSession session, ChatMessage chatMessage) {
        if (chatMessage.getMessageType() == MessageType.ENTER) {
            if (webSocketHandler.addSession(session)) {
                chatMessage.setText("👋🏻 " + chatMessage.getSender() + " 님이 입장했습니다.");
            }
        }
        sendMessage(session, chatMessage);
    }

    //    @Transactional
    private <T> void sendMessage(WebSocketSession session, ChatMessage chatMessage) {
//        try{
//            session.sendMessage(chatMessage);

//        } catch (IOException e){

//        }
    }

    @Transactional
    public ChatroomResponse createChatroom(CreateChatroomRequest request) {
        Chatroom chatroom = chatroomRepository.save(
                Chatroom.builder()
                        .name(request.getName())
                        .build());
        return new ChatroomResponse(chatroom.getId());
    }

    public FindChatroomsResponse findChatrooms(String name) {
        List<Chatroom> chatrooms = chatroomRepository.findByName(name);
        List<FindChatroomResponse> findChatroomRespons = chatrooms.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return new FindChatroomsResponse(findChatroomRespons);
    }


    private FindChatroomResponse toResponse(Chatroom chatroom) {
        return new FindChatroomResponse(chatroom.getId(), chatroom.getName());
    }

    @Transactional
    public ChatroomResponse delete(Long id) {
        Chatroom chatroom = chatroomRepository.findById(id)
                .orElseThrow(NotFoundMemberException::new);
        ChatroomResponse response = new ChatroomResponse(chatroom.getId());
        chatroomRepository.delete(chatroom);
        return response;
    }

    @Transactional
    public void updateName(Long id, String newName) {

        Chatroom chatroom = chatroomRepository.findById(id)
                .orElseThrow(NotFoundMemberException::new);
        chatroom.updateName(newName);
    }
}
