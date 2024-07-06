package kuchat.server.domain.enums;

import kuchat.server.common.exception.KuchatException;
import lombok.Getter;

import java.util.Arrays;

import static kuchat.server.common.exception.BaseResponse.LANGUAGE_NOTFOUND;

@Getter
public enum SettingLanguage {
    ENGLISH("영어"),
    KOREAN("한국어");

    private String value;

    SettingLanguage(String value) {
        this.value = value;
    }

    public static SettingLanguage of(String value) {

        return Arrays.stream(values())
                .filter(language -> value.equals(language.getValue()))
                .findFirst()
                .orElseThrow(() -> new KuchatException(LANGUAGE_NOTFOUND));
    }


}
