package kuchat.server.domain.enums;

import kuchat.server.common.exception.KuchatException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static kuchat.server.common.exception.BaseResponse.NOT_FOUND_PLATFORM;

@Slf4j
@Getter
public enum Platform {
    GOOGLE("google"),
    NAVER("naver"),
    KAKAO("kakao");

    private String value;

    Platform(String value) {
        this.value = value;
    }

    public static Platform of(String value) {
        log.info("[of] platform string = {}", value);
        for (Platform platform : Platform.values()) {
            if (platform.getValue().equals(value)) {
                return platform;
            }
        }
        throw new KuchatException(NOT_FOUND_PLATFORM);
    }
}
