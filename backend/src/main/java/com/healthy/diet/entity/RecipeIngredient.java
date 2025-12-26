package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("recipe_ingredients")
public class RecipeIngredient {
    @TableId
    private Long id;
    private Long recipeId;
    private String name;
    private String amount;
}



