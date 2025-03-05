package kuchat.server.domain.auth.controller;

import kuchat.server.domain.auth.JwtTokenService;
import kuchat.server.domain.auth.dto.AuthTokenResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static kuchat.server.domain.enums.Role.STUDENT;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
@RestController
public class AuthController {

    private final JwtTokenService jwtTokenService;

    @PostMapping("/refresh-token")
    public ResponseEntity<?> reissueToken(@RequestHeader(value = "Authorization", required = false) String refreshToken) {
        Long memberId = jwtTokenService.validateRefreshToken(refreshToken);
        AuthTokenResponse response = jwtTokenService.generateAuthToken(STUDENT, memberId);
        return ResponseEntity.ok(response);
    }
}
