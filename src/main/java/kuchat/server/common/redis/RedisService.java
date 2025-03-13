package kuchat.server.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;       // member id - refresh token
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:"; // 키 prefix


    public String getRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        String refreshToken = Objects.requireNonNull(redisTemplate.opsForValue().get(key)).toString();
        log.info("[getRefreshToken] member id = {} 의 리프레시 토큰 조회 = {}", memberId, refreshToken);
        return refreshToken;
    }

    public void setRefreshToken(Long memberId, String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.opsForValue().set(key, refreshToken);         // 이 때 저장되는 refresh token에는 bearer이 붙어있으면 안됨
    }

    public void removeRefreshToken(Long memberId) {
        String key = REFRESH_TOKEN_PREFIX + memberId;
        redisTemplate.delete(key);
        log.info("Redis 삭제: {}", key);
    }

}
