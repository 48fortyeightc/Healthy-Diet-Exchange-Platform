package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("v_recipe_details")
public class RecipeDetail {
    @TableId
    private Long id;
    private String title;
    private String coverImage;
    private String difficulty;
    private Integer cookingTime;
    private String status;
    private String authorName;
    private String authorPhoneMasked;
    private Integer likeCount;
    private Integer commentCount;
}



