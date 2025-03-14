package kuchat.server.common.redis;

import com.fasterxml.jackson.databind.ObjectMapper;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.response.BaseResponseStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static kuchat.server.common.redis.RedisKey.*;
import static kuchat.server.common.response.BaseResponseStatus.*;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
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
        log.info("[removeRefreshToken] reids에서 리프레시 토큰 삭제 member id = {}, 삭제된 항목 개수 = {}",
                memberId, num);
    }

    public RedisMemberChatInfo getMemberChatInfo(Long memberId, Long chatId) {
        Object object = hashOperations.get(MEMBER_KEY.getKey(memberId), CHAT_KEY.getKey(chatId));
        if (object == null){
            throw new KuchatException(REDIS_FIND_FAIL);
        }
        RedisMemberChatInfo memberChatInfo = objectMapper.convertValue(object, RedisMemberChatInfo.class);
        log.info("[getMemberChatInfo] member id = {} , chat id = {} 의 채팅방 정보 조회 = {}", memberId, chatId, memberChatInfo);
        return memberChatInfo;
    }

    public void setMemberChatInfo(Long memberId, Long chatId, RedisMemberChatInfo memberChatInfo) {
        log.info("[setMemberChatInfo] member = {} 의 채팅방{} 정보 저장 = {}", memberId, chatId, memberChatInfo);
        hashOperations.put(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey(), memberChatInfo);
    }

    public void removeMemberChatInfo(Long memberId, Long chatId) {
        Long num = hashOperations.delete(MEMBER_KEY.getKey(memberId), REFRESH_TOKEN_KEY.getKey());
        log.info("[removeMemberChatInfo] reids에서 member id = {}의 채팅방 = {} 정보 삭제 : {}",
                memberId, chatId, num);
    }
}
