package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("comments")
public class Comment {
    @TableId
    private Long id;
    private Long userId;
    private Long recipeId;
    private Long parentId;
    private String content;
    private LocalDateTime createdAt;
}



