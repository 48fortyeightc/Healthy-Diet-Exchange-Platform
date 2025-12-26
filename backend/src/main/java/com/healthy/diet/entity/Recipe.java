package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("recipes")
public class Recipe {
    @TableId
    private Long id;
    private Long authorId;
    private String title;
    private String description;
    private String coverImage;
    private String difficulty;
    private Integer cookingTime;
    private String status;
    private LocalDateTime createdAt;
}



