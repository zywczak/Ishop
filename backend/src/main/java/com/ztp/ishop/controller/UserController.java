package com.ztp.ishop.controller;

import com.ztp.ishop.dto.UserProfileDto;
import com.ztp.ishop.entity.User;
// import com.ztp.ishop.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/profile")
    public ResponseEntity<UserProfileDto> getUserProfile(@AuthenticationPrincipal User user) {
        UserProfileDto userProfileDto = UserProfileDto.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .build();

        return ResponseEntity.ok(userProfileDto);
    }
}

