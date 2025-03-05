package kuchat.server.domain;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kuchat.server.common.exception.KuchatException;
import kuchat.server.domain.auth.service.JwtTokenService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class HomeController {

    private final JwtTokenService jwtTokenService;

    @GetMapping("/")
    public String home(@CookieValue(name = "Authorization", required = false) String token,
                       Model model, HttpServletResponse response) {
        log.info("[home] 홈화면으로 이동");
        try {
            Member member = jwtTokenService.extractMemberByAccessToken(token);
            log.info("[home] 쿠키에 담긴 회원 정보 = {}", member.toString());
            model.addAttribute("member", member);
        } catch (KuchatException e) {
            Cookie cookie = new Cookie("Authorization", "");
            cookie.setPath("/");
            cookie.setMaxAge(0);
            response.addCookie(cookie);
        }
        return "index";
    }

//    @GetMapping("/member/signup")
//    public String signup(Model model) {
//        log.info("[signup] 토큰이 없어도 회원가입 페이지로 이동 가능");
//        List<String> languages = Arrays.stream(LearnLanguage.values())
//                .map(LearnLanguage::getValue)
//                .toList();
//        model.addAttribute("languages", languages);
//
//        List<String> settingLanguages = SettingLanguage.getValues();
//        model.addAttribute("settingLanguages", settingLanguages);
//
//        return "signup";
//    }

    @GetMapping("/room")
    public String chatroomList() {
        log.info("[chatroomList] 채팅방 목록 조회");
        return "chatroom";
    }

}
