package kuchat.server.domain.enums;

import lombok.Getter;

@Getter
public enum Gender {
    MALE("남성"),
    FEMALE("여성");

    private String korean;

    Gender(String value) {
        this.korean = korean;
    }

    public static Gender of(String korean) {
        if (korean.equals("남성")) {
            return MALE;
        }
        return FEMALE;
    }
}
