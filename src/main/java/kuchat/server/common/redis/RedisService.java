package kuchat.server.common.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

import static kuchat.server.common.redis.RedisKey.MEMBER_KEY;
import static kuchat.server.common.redis.RedisKey.REFRESH_TOKEN_KEY;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final HashOperations<String, String, Object> hashOperations;


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

}
