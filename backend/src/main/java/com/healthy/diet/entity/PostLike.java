package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("post_likes")
public class PostLike {
    private Long userId;
    private Long postId;
    private LocalDateTime createdAt;
}

