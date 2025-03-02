package kuchat.server.domain.enums;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

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
        return null;
    }

    public String getValue(){
        return value.toLowerCase();
    }
}
