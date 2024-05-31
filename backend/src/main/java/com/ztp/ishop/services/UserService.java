package com.ztp.ishop.services;

import com.ztp.ishop.dto.CredentialsDto;
import com.ztp.ishop.dto.SignUpDto;
import com.ztp.ishop.dto.UserDTO;
import com.ztp.ishop.entity.User;
import com.ztp.ishop.enums.Role;
import com.ztp.ishop.exceptions.AppException;
import com.ztp.ishop.mappers.UserMapper;
import com.ztp.ishop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import org.springframework.security.core.Authentication;

import java.nio.CharBuffer;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    public UserDTO login(CredentialsDto credentialsDto) {
        User user = userRepository.findByEmail(credentialsDto.getEmail())
                .orElseThrow(() -> new AppException("Unknown user33333333333333333333333333333", HttpStatus.NOT_FOUND));

        if (passwordEncoder.matches(CharBuffer.wrap(credentialsDto.getPassword()), user.getPassword())) {
            return userMapper.toUserDto(user);
        }
        throw new AppException("Invalid password", HttpStatus.BAD_REQUEST);
    }

    public UserDTO register(SignUpDto userDto) {
        Optional<User> optionalUser = userRepository.findByEmail(userDto.getEmail());

        if (optionalUser.isPresent()) {
            throw new AppException("Login already exists", HttpStatus.BAD_REQUEST);
        }

        User user = userMapper.signUpToUser(userDto);
        user.setType(Role.USER);
        user.setPassword(passwordEncoder.encode(CharBuffer.wrap(userDto.getPassword())));

        User savedUser = userRepository.save(user);

        return userMapper.toUserDto(savedUser);
    }

    public UserDTO findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Unknown user1111111111111111111", HttpStatus.NOT_FOUND));
        return userMapper.toUserDto(user);
    }

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDTO) {
            String email = ((UserDTO) authentication.getPrincipal()).getEmail();
            return userRepository.findByEmail(email).orElseThrow(() -> new AppException("Unknown user222222222222222222", HttpStatus.NOT_FOUND));
        }
        throw new AppException("User not logged in", HttpStatus.UNAUTHORIZED);
    }

}
