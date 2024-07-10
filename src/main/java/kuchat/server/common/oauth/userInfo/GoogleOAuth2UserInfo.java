package kuchat.server.common.oauth.userInfo;

import java.util.Map;

public class GoogleOAuth2UserInfo extends OAuth2UserInfo{

    public GoogleOAuth2UserInfo(Map<String, Object> attributes) {
        // attributes = {sub=구글에서 부여한 id, name=lys, given_name=lys, picture=구글프로필url, email=구글메일, email_verified=true}
        super(attributes);
    }

    @Override
    public String getId() {
        return (String) attributes.get("sub");
    }

    @Override
    public String getImageUrl() {
        return (String) attributes.get("picture");
    }

    @Override
    public String getEmail(){
        return (String) attributes.get("email");
    }
}
