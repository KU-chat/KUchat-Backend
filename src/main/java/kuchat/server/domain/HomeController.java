package kuchat.server.domain;

import kuchat.server.common.exception.JwtTokenException;
import kuchat.server.common.jwt.JwtTokenService;
import kuchat.server.common.jwt.argumentResolver.Auth;
import kuchat.server.domain.member.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static kuchat.server.common.response.BaseResponseStatus.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class HomeController {

    private final JwtTokenService jwtTokenService;

    @GetMapping("")
    public String home(@RequestHeader(value = "Authorization", required = false) String auth, Model model) {
        if (auth != null) {
            Member member = jwtTokenService.extractMemberByAccessToken(auth);
            model.addAttribute("member", member);
        }
        log.info("[home] 홈화면으로 이동~");
        log.info("[home] model = {}", model);
        return "index";
    }

    // 회원 정보 받기
    @GetMapping("/member/signup")
    public String signup(){
        log.info("[signup] 토큰이 없어도 회원가입 페이지로 이동 가능");
        return "signup";
    }
}
