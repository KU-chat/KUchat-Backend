package kuchat.server.common.redis;

import kuchat.server.common.exception.KuchatException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static kuchat.server.common.exception.BaseResponse.REDIS_FIND_FAIL;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
//@Component
@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;

    // 1. ValueOperations : key - value
    @Transactional
    public void putValueOp(String key, String value) {
        log.info("[putValueOp] key = {}, value = {}", key, value);
        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
        valueOps.set(key, value);
    }

    public String getValueOp(String key) {
        String value = (String) redisTemplate.opsForValue().get(key);
        log.info("[getValueOp] key = {}, value = {}", key, value);
        return value;
    }

    @Transactional
    public boolean deleteValueOp(String key) {
        log.info("[deleteValueOp] 제거하는 key = {}", key);
        return Boolean.TRUE.equals(redisTemplate.delete(key));
    }


    // 2. HashOperations : key - hash key - value
    @Transactional
    public void putHashOp(String key, Long hashKey, String value) {
        log.info("[putHashOp] key = {}, hash key = {}, value = {}", key, hashKey.toString(), value);
        HashOperations<String, Long, String> hashOps = redisTemplate.opsForHash();
        hashOps.put(key, hashKey, value);
    }

    @Transactional
    public void putHashOps(String key, Map<Long, String> data) {
        log.info("[putHashOps] key = {}, Map <hash key, value> = {}", key, data.toString());
        HashOperations<String, Long, String> values = redisTemplate.opsForHash();
        values.putAll(key, data);
    }

    public String getHashOp(String key, Long hashKey) {
        String value = (String) redisTemplate.opsForHash().get(key, hashKey);
        log.info("[getHashOp] 조회하는 key = {}, hashKey = {}, value = {}", key, hashKey, value);
        return value;
    }

    public Map<Long, String> getHashOpMap(String key) {
        Map<Object, Object> rawMap = redisTemplate.opsForHash().entries(key);
        Map<Long, String> result = new HashMap<>();
        for (Map.Entry<Object, Object> entry : rawMap.entrySet()) {
            try {
                Long keyAsLong = (Long) entry.getKey();
                String valueAsString = (String) entry.getValue();
                result.put(keyAsLong, valueAsString);
            } catch (Exception e) {
                throw new KuchatException(REDIS_FIND_FAIL);
            }
        }
        return result;
    }

    @Transactional
    public void deleteHashOp(String key, String hashKey) {
        log.info("[deleteHashOp] 제거하는 key = {}, hashKey = {}", key, hashKey);
        HashOperations<String, Object, Object> values = redisTemplate.opsForHash();
        values.delete(key, hashKey);
    }

}
