package kuchat.server.domain.enums;

import kuchat.server.common.exception.KuchatException;
import lombok.Getter;

import java.util.Arrays;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_LANGUAGE;

@Getter
public enum LearnLanguage {
    KOREAN("한국어"),
    ENGLISH("영어"),
    CHINESE("중국어"),
    JAPANESE("일본어"),
    RUSSIAN("러시아어"),
    VIETNAMESE("베트남어"),
    INDOMALAYAN("인도네시아어"),        // 인도네시아어
    MONGOLIAN("몽골어");

    private String value;

    LearnLanguage(String value) {
        this.value = value;
    }

    public static LearnLanguage of(String value) {

        return Arrays.stream(values())
                .filter(language -> value.equals(language.getValue()))
                .findFirst()
                .orElseThrow(() -> new KuchatException(NOT_FOUND_LANGUAGE));
    }
}
