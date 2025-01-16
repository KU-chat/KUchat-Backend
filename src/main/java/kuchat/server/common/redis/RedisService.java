package kuchat.server.common.redis;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {

    private Map<Long, String> memberInfo;      // member id - refresh token

    @PostConstruct
    private void init() {
        memberInfo = new ConcurrentHashMap<>();
    }

    public String getRefreshToken(Long memberId) {
        return memberInfo.get(memberId);
    }

    public void setRefreshToken(Long memberId, String refreshToken) {
        memberInfo.put(memberId, refreshToken);     // 이 때 저장되는 refresh token에는 bearer이 붙어있으면 안됨
    }

    public void removeRefreshToken(Long memberId) {
        memberInfo.remove(memberId);
    }

}
