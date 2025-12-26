package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("recipe_steps")
public class RecipeStep {
    @TableId
    private Long id;
    private Long recipeId;
    private Integer stepNumber;
    private String instruction;
    private String imageUrl;
}



