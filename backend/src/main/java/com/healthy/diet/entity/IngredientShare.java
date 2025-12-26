package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("ingredient_shares")
public class IngredientShare {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long providerId;
    private String name;
    private String quantity;
    private LocalDate expiryDate;
    private String region;
    private String status;
    private LocalDateTime createdAt;
}
