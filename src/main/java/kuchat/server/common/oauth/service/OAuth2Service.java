package kuchat.server.common.oauth.service;

import kuchat.server.common.oauth.CustomOAuth2User;
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
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;

    // 구글이 발급한 엑세스 토큰을 가지고 구글에게 사용자 정보 요청하기
    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        log.info("[loadUser] 구글 소셜 로그인을 완료한 사용자의 정보 후처리 시작!!");
        log.info("[loadUser] userRequest clientRegistration = {}", userRequest.getClientRegistration());
        log.info("[loadUser] userRequest accessToken = {}", userRequest.getAccessToken().getTokenValue());
        log.info("[loadUser] userRequest attributes = {}", oAuth2User.getAttributes());


        // 1. oAuth2User 객체에서 사용자 정보 추출하기
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String provider = userRequest.getClientRegistration().getRegistrationId();      // = "google"
        Platform platform = Platform.of(provider);
        String providerId = (String) attributes.get("sub");          // 구글 플래폼에서의 pk
        String email = (String) attributes.get("email");
        String profileImage = (String) attributes.get("picture");

        // 2. 추출한 정보를 가지고
        //      - 기존 멤버라면 repository 에서 가져오기
        //      - 새로운 멤버라면 생성 후 save 하기
        Member member = memberRepository.findByPlatformAndProviderId(platform, providerId)
                .orElseGet(() -> {
                    Member newMember = Member.builder()
                            .platform(platform)
                            .providerId(providerId)
                            .email(email)
                            .profileImage(profileImage)
                            .build();
                    return memberRepository.save(newMember);
                });
        log.info("[loadUser] member = {}", member.toString());

        String attributeKey = userRequest.getClientRegistration()      // 구글의 경우 attributeKey 가 "sub"이다.
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();

        // 3. 여기서 리턴되는 객체가 security session 에 들어감 (컨트롤러에서 추출하여 사용 가능)
        return CustomOAuth2User.builder()
                .authorities(Collections.singleton(new SimpleGrantedAuthority(member.getRole().getKey())))
                .attributes(attributes)
                .nameAttributeKey(attributeKey)
                .email(email)
                .role(member.getRole())
                .platform(platform)
                .providerId(providerId).build();
    }
}
