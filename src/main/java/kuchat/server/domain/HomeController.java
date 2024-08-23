package kuchat.server.domain;

import kuchat.server.common.exception.JwtTokenException;
import kuchat.server.common.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import static kuchat.server.common.exception.BaseResponse.INVALID_TOKEN;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class HomeController {

    private final JwtTokenService jwtTokenService;

    @GetMapping("")
    public String home() {
        log.info("[home] 홈화면으로 이동~");
        return "index";
    }

    // 회원 정보 받기
    @GetMapping("/member/signup")
    public String signup(@RequestHeader(HttpHeaders.AUTHORIZATION) String guestToken,
                         Model model) {
        String token = guestToken.trim().replace("Bearer ", "");
        log.info("guestToken = {}", token);

        if (!jwtTokenService.isValidGuestToken(token)) {
            log.info("[signup] guestToken이 유효하지 않습니다.");
            throw new JwtTokenException(INVALID_TOKEN);
        }
        log.info("[signup] 유효한 토큰이 들어와서 회원가입 페이지로 이동");
        model.addAttribute("guestToken", guestToken);
        return "signup";
    }
}
