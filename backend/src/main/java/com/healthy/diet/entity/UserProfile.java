package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("user_profiles")
public class UserProfile {
    @TableId
    private Long id;
    private Long userId;
    private String nickname;
    private String avatarUrl;
    private String gender;
    private String bio;
}

