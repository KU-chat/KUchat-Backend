package kuchat.server.domain;

import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class HomeController {

    private final JwtTokenService jwtTokenService;

    @GetMapping("")
    public String home(@CookieValue(name = "Authorization", required = false) String auth, Model model) {
        if (auth != null) {
            log.info("[home] auth = {}", auth);
            Member member = jwtTokenService.extractMemberByAccessToken(auth);
            model.addAttribute("member", member);
        }
        log.info("[home] 홈화면으로 이동~");
        log.info("[home] 쿠키에 담긴 토큰 = {}", auth);
        log.info("[home] model = {}", model);
        return "index";
    }

    @GetMapping("/member/signup")
    public String signup() {
        log.info("[signup] 토큰이 없어도 회원가입 페이지로 이동 가능");
        return "signup";
    }
}
