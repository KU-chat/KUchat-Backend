package kuchat.server.domain.enums;

import kuchat.server.common.exception.KuchatException;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

import static kuchat.server.common.response.BaseResponseStatus.NOT_FOUND_LANGUAGE;

@Getter
public enum SettingLanguage {
    ENGLISH(List.of("영어", "english")),
    KOREAN(List.of("한국어", "korean"));

    private List<String> aliases;

    SettingLanguage(List<String> aliases) {
        this.aliases = aliases;
    }

    public static SettingLanguage of(String setting) {
        return Arrays.stream(values())
                .filter(language -> language.aliases.contains(setting))
                .findFirst()
                .orElseThrow(() -> new KuchatException(NOT_FOUND_LANGUAGE));
    }

    public static List<String> getValues() {
        return Arrays.stream(values())
                .map(language -> language.aliases.get(0))
                .toList();
    }
}
