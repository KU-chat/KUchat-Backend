package kuchat.server.common.oauth.service;

import kuchat.server.common.oauth.CustomOAuth2User;
import kuchat.server.common.oauth.dto.OAuth2Attribute;
import kuchat.server.domain.enums.Platform;
import kuchat.server.domain.member.Member;
import kuchat.server.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    // 구글이 발급한 엑세스 토큰을 가지고 구글에게 사용자 정보 요청하기
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("[loadUser] 구글이 발급한 엑세스 토큰을 가지고 구글에게 사용자 정보 요청하기");

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();      // 구글/카카오/네이버에서 사용자의 pk (id) = attributeValue
        Platform platform = Platform.of(registrationId);
        String attributeKey = userRequest.getClientRegistration()      // 구글의 경우 attributeKey 가 "sub"이다.
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        String attributeValue = oAuth2User.getAttribute(attributeKey);       // 구글/카카오/네이버에서 사용자의 pk (id)
        Map<String, Object> attributes = oAuth2User.getAttributes();
        OAuth2Attribute oAuth2Attribute = OAuth2Attribute.of(platform, attributeKey, oAuth2User.getAttributes());
        log.info("[loadUser] member saveOrUpdate 전 oAuth2Attribute = {}", oAuth2Attribute.toString());
        Member member = saveOrUpdate(oAuth2Attribute);

        return new CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())),
                attributes,
                attributeKey,
                member.getEmail(),
                member.getRole(),
                member.getPlatform()
        );
    }

    private Member saveOrUpdate(OAuth2Attribute oAuth2Attribute) {
        Platform platform = oAuth2Attribute.getPlatform();
        String attributeValue = oAuth2Attribute.getAttributeValue();

        Optional<Member> optionalMember = memberRepository.findByPlatformAndAttributeName(platform, attributeValue);

        if(optionalMember.isPresent()){
            Member member = optionalMember.get();
            log.info("[loadUser] 기존에 있던 member : "+member.toString());
            return member;
        }

        Member member = oAuth2Attribute.toMember(platform, attributeValue, oAuth2Attribute.getOAuth2UserInfo());
        log.info("[loadUser] 새로 만든 member : "+member.toString());
        return memberRepository.save(member);
    }
}
