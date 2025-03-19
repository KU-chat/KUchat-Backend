package kuchat.server.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.KuchatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Objects;

import static kuchat.server.common.redis.RedisKey.*;
import static kuchat.server.common.response.BaseResponseStatus.REDIS_FIND_FAIL;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {

    private final HashOperations<String, String, Object> hashOperations;
    private final ObjectMapper objectMapper;


    public String getRefreshToken(Long memberId) {
        String refreshToken = Objects.requireNonNull(
                        hashOperations.get(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey()))
                .toString();
        log.info("[getRefreshToken] member id = {} 의 리프레시 토큰 조회 = {}", memberId, refreshToken);
        return refreshToken;
    }

    public void setRefreshToken(Long memberId, String refreshToken) {
        log.info("[setRefreshToken] member = {} 의 리프레시 토큰 저장 = {}", memberId, refreshToken);
        hashOperations.put(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey(), refreshToken);
    }

    public void removeRefreshToken(Long memberId) {
        Long num = hashOperations.delete(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey());
        log.info("[removeRefreshToken] 리프레시 토큰 삭제 member id = {}, 삭제된 항목 개수 = {}", memberId, num);
    }

    // TODO. 채팅방 목록 조회 : 채팅방에 메시지가 왔을 경우, 다른 사용자가 접속 중인지 확인 후 알림을 보내거나 or 메시지를 바로 보내는 분기처리ㄱㄱ
    public RedisMemberChatInfo getMemberChatInfo(Long memberId, Long chatId) {
        Object object = hashOperations.get(MEMBER_KEY.getKey(memberId), CHAT_KEY.getKey(chatId));
        if (object == null) {
            throw new KuchatException(REDIS_FIND_FAIL);
        }
        RedisMemberChatInfo memberChatInfo = objectMapper.convertValue(object, RedisMemberChatInfo.class);
        log.info("[getMemberChatInfo] member id = {} , chat id = {} 의 채팅방 정보 조회 = {}", memberId, chatId, memberChatInfo);
        return memberChatInfo;
    }

    // TODO. 사용자가 채팅을 보낼 때 이 API로 캐시 갱신
    public void setMemberChatInfo(Long memberId, Long chatId, RedisMemberChatInfo memberChatInfo) {
        log.info("[setMemberChatInfo] member = {} 의 채팅방 = {} 정보 저장 = {}", memberId, chatId, memberChatInfo);
        hashOperations.put(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey(), memberChatInfo);
    }

    // TODO. 사용자가 채팅방을 나갈 때 이 API로 캐시 제거
    public void removeMemberChatInfo(Long memberId, Long chatId) {
        Long num = hashOperations.delete(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey());
        log.info("[removeMemberChatInfo] member id = {}의 채팅방 = {} 정보 삭제. 삭제된 항목 개수 = {}",
                memberId, chatId, num);
    }

    // TODO. 채팅방 목록 조회 : 이 API 사용해서 데이터 같이 보내야함
    public RedisChatInfo getChatInfo(Long chatId) {
        Map<String, Object> entries = hashOperations.entries(CHAT_KEY.getKey(chatId));
        if (entries.isEmpty()) {
            throw new KuchatException(REDIS_FIND_FAIL);
        }
        RedisChatInfo chatInfo = objectMapper.convertValue(entries, RedisChatInfo.class);
        log.info("[getChatInfo] chat id = {} 의 채팅방 정보 조회 = {}", chatId, chatInfo);
        return chatInfo;
    }

    // TODO. 사용자가 채팅을 보낼 때 이 API로 최근 메시지 캐시 갱신
    public void setChatInfo(Long memberId, Long chatId, RedisChatInfo memberChatInfo) {
        log.info("[setChatInfo] member = {} 의 채팅방{} 정보 저장 = {}", memberId, chatId, memberChatInfo);
        hashOperations.put(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey(), memberChatInfo);
    }

    // TODO. 채팅방을 모두 나가서 회원이 0명이 되는 경우 캐시 제거
    public void removeChatInfo(Long chatId) {
        Long num = hashOperations.delete(REFRESH_TOKEN_KEY.getKey());
        log.info("[removeChatInfo] 채팅방 = {} 정보 삭제. 삭제된 항목 개수 = {}", chatId, num);
    }
}
