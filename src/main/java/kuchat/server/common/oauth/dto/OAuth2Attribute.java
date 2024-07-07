package kuchat.server.common.oauth.dto;

import kuchat.server.common.exception.KuchatException;
import kuchat.server.common.oauth.userInfo.GoogleOAuth2UserInfo;
import kuchat.server.common.oauth.userInfo.OAuth2UserInfo;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.member.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.UUID;

import static kuchat.server.common.exception.BaseResponse.NOTFOUND_PLATFORM;

@Slf4j
@Getter
@ToString
public class OAuth2Attribute {

    private String attributeKey;
    private String attributeValue;
    private OAuth2UserInfo oAuth2UserInfo;
    private Platform platform;

    @Builder
    public OAuth2Attribute(String attributeKey, OAuth2UserInfo oAuth2UserInfo, Platform platform) {

        log.info("[OAuth2Attribute 생성자] attributeKey = {}, attributeValue = {}, oAuth2UserInfo = {}, platform = {}",
                attributeKey, oAuth2UserInfo.getId(), oAuth2UserInfo.toString(), platform);

        this.attributeKey = attributeKey;
        this.attributeValue = oAuth2UserInfo.getId();
        this.oAuth2UserInfo = oAuth2UserInfo;
        this.platform = platform;
    }

    public static OAuth2Attribute of(Platform platform, String attributeKey,
                                     Map<String, Object> attributes) {
        log.info("[of] platform = {}, attributeKey = {}, attributes = {}",
                platform, attributeKey, attributes.toString());
        if (platform == Platform.GOOGLE) {
            return ofGoogle(attributeKey, attributes);
        }
        throw new KuchatException(NOTFOUND_PLATFORM);
    }

    private static OAuth2Attribute ofGoogle(String attributeKey, Map<String, Object> attributes) {
        log.info("[ofGoogle] attributeKey = {}", attributeKey);

        return OAuth2Attribute.builder()
                .attributeKey(attributeKey)
                .oAuth2UserInfo(new GoogleOAuth2UserInfo(attributes))
                .platform(Platform.GOOGLE)
                .build();
    }

    public Member toMember(Platform platform, String attributeName, OAuth2UserInfo oAuth2UserInfo) {
        return Member.builder()
                .email(UUID.randomUUID() + "@socialUser.com")
                .platform(platform)
                .attributeName(attributeName)
                .build();
    }
}
