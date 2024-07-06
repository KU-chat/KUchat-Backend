package kuchat.server.common;

import kuchat.server.domain.jwt.JwtTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    @GetMapping("member/signup")
    public String signup(@RequestParam("platform") String platform,
                         @RequestParam("attributeName") String attributeName,
                         Model model) {
        log.info("[signup] 신규회원 정보 받는 화면으로 이동!");
        log.info("[signup] platform = {}, attributeName = {}", platform, attributeName);
        model.addAttribute("platform", platform);
        model.addAttribute("attributeName", attributeName);

        return "signup";
    }
}
