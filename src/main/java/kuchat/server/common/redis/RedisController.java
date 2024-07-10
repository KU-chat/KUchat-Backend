package kuchat.server.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class RedisController {

    private final RedisService redisService;

    // 1. ValueOperations
    //    "refreshToken: {memberId}" : "{refreshToken}"

    // 2. ValueOperations
    //    "session: {memberId}" : "{sessionId}"

    // 3. HashOperations (한 채팅방에 대한 정보)
    //    "chatroomId : {id}"
    //      - "{memberId}" : "{세션 id}"
    //      - "{memberId}" : "{세션 id}"


    public void putRefreshToken(Long memberId, String refreshToken) {
        redisService.putValueOp("refreshToken: " + memberId, refreshToken);
    }

    public boolean putMemberSession(Long memberId, String sessionId) {
        redisService.putValueOp("session: " + memberId, sessionId);
        return true;
    }

    public String getRefreshToken(Long memberId) {
        return redisService.getValueOp("refreshToken: " + memberId);
    }

    public String getMemberSession(Long memberId) {
        return redisService.getValueOp("session: " + memberId);
    }

    public void putChatroomSession(Long chatroomId, Long memberId, String sessionId) {
        redisService.putHashOp("chatroom: " + chatroomId, memberId, sessionId);
    }

    public void putChatroomSessions(Long chatroomId, Map<Long, String> map){
        redisService.putHashOps("chatroom: " + chatroomId, map);
    }

    // 한 채팅방에 있는 모든 <member id, session id> 쌍을 반환
    public Map<Long, String> getChatroomSessions(Long chatroomId) {
        return redisService.getHashOpMap("chatroom: " + chatroomId);
    }

    public boolean deleteChatroomSession(Long chatroomId, Long memberId) {
        redisService.deleteHashOp("chatroom: " + chatroomId, memberId.toString());
        return true;
    }

}
