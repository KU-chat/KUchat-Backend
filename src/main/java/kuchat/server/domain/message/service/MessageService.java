package kuchat.server.domain.message.service;

import kuchat.server.domain.message.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Transactional
@Service
public class MessageService {

    public void findRecentMessages(Long chatroomId) {
    }
}
