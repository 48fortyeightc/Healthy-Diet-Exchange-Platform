package com.healthy.diet.controller;

import com.healthy.diet.entity.User;
import com.healthy.diet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @PostMapping("/register")
    public String register(@RequestBody UserRegistrationDto dto) {
        userMapper.registerUser(
            dto.getUsername(), 
            dto.getPassword(), // 实际应在Service层加密
            dto.getEmail(), 
            dto.getPhone(), 
            dto.getNickname()
        );
        return "注册成功";
    }

    // 内部类作为 DTO
    @lombok.Data
    public static class UserRegistrationDto {
        private String username;
        private String password;
        private String email;
        private String phone;
        private String nickname;
    }
}



