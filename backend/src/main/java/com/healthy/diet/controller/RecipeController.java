package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.healthy.diet.entity.*;
import com.healthy.diet.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    @Autowired
    private RecipeDetailMapper recipeDetailMapper;
    
    @Autowired
    private RecipeMapper recipeMapper;
    
    @Autowired
    private RecipeStepMapper recipeStepMapper;
    
    @Autowired
    private RecipeIngredientMapper recipeIngredientMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;

    @GetMapping("/list")
    public Page<RecipeDetail> list(@RequestParam(defaultValue = "1") Integer page, 
                                  @RequestParam(defaultValue = "20") Integer size) {
        Page<RecipeDetail> pageParam = new Page<>(page, size);
        return recipeDetailMapper.selectPage(pageParam, new QueryWrapper<RecipeDetail>().eq("status", "PUBLISHED"));
    }

    /**
     * 获取我的作品列表
     */
    @GetMapping("/my")
    public List<Recipe> getMyRecipes(@RequestParam Long userId) {
        return recipeMapper.selectList(new QueryWrapper<Recipe>()
                .eq("author_id", userId)
                .orderByDesc("created_at"));
    }

    /**
     * 删除作品 (级联删除相关数据)
     */
    @DeleteMapping("/delete/{id}")
    @Transactional
    public String deleteRecipe(@PathVariable Long id) {
        // 1. 删除关联的步骤
        recipeStepMapper.delete(new QueryWrapper<RecipeStep>().eq("recipe_id", id));
        // 2. 删除关联的食材
        recipeIngredientMapper.delete(new QueryWrapper<RecipeIngredient>().eq("recipe_id", id));
        // 3. 删除食谱主表
        recipeMapper.deleteById(id);
        return "作品已删除";
    }

    /**
     * 核心功能：发布食谱 (支持草稿和正式发布)
     */
    @PostMapping("/publish")
    @Transactional
    public String publishRecipe(@RequestBody Map<String, Object> params) {
        try {
            // 1. 保存食谱主表
            Recipe recipe = new Recipe();
            if (params.get("id") != null) {
                recipe.setId(Long.valueOf(params.get("id").toString()));
            }
            recipe.setAuthorId(Long.valueOf(params.get("authorId").toString()));
            recipe.setTitle(params.get("title").toString());
            recipe.setDescription(params.get("description").toString());
            recipe.setDifficulty(params.get("difficulty").toString());
            recipe.setCookingTime(Integer.valueOf(params.get("cookingTime").toString()));
            recipe.setCoverImage(params.get("coverImage") != null ? params.get("coverImage").toString() : "https://cp1.douguo.com/upload/caiku/3/2/5/220x220_321471e1a1c6ca75bbea319ebb6cb025.jpg");
            
            String status = params.get("isDraft") != null && (Boolean)params.get("isDraft") ? "DRAFT" : "PUBLISHED";
            recipe.setStatus(status);
            recipe.setCreatedAt(java.time.LocalDateTime.now());
            
            if (recipe.getId() != null) {
                recipeMapper.updateById(recipe);
                // 更新时先清空旧的步骤和食材
                recipeStepMapper.delete(new QueryWrapper<RecipeStep>().eq("recipe_id", recipe.getId()));
                recipeIngredientMapper.delete(new QueryWrapper<RecipeIngredient>().eq("recipe_id", recipe.getId()));
            } else {
                recipeMapper.insert(recipe);
            }
            
            Long recipeId = recipe.getId();

            // 2. 保存食材清单
            List<Map<String, String>> ingredients = (List<Map<String, String>>) params.get("ingredients");
            for (Map<String, String> ing : ingredients) {
                RecipeIngredient ri = new RecipeIngredient();
                ri.setRecipeId(recipeId);
                ri.setName(ing.get("name"));
                ri.setAmount(ing.get("amount"));
                recipeIngredientMapper.insert(ri);
            }

            // 3. 保存烹饪步骤
            List<String> steps = (List<String>) params.get("steps");
            for (int i = 0; i < steps.size(); i++) {
                RecipeStep rs = new RecipeStep();
                rs.setRecipeId(recipeId);
                rs.setStepNumber(i + 1);
                rs.setInstruction(steps.get(i));
                recipeStepMapper.insert(rs);
            }

            return status.equals("DRAFT") ? "已保存到草稿箱" : "发布成功！";
        } catch (Exception e) {
            return "操作失败：" + e.getMessage();
        }
    }

    @GetMapping("/isLiked")
    public Boolean checkIsLiked(@RequestParam Long userId, @RequestParam Long recipeId) {
        return false; // 简化展示逻辑
    }

    @GetMapping("/{id}")
    public Map<String, Object> getDetail(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        QueryWrapper<RecipeDetail> detailWrapper = new QueryWrapper<>();
        detailWrapper.eq("id", id);
        result.put("detail", recipeDetailMapper.selectOne(detailWrapper));
        
        result.put("steps", recipeStepMapper.selectList(new QueryWrapper<RecipeStep>().eq("recipe_id", id).orderByAsc("step_number")));
        result.put("ingredients", recipeIngredientMapper.selectList(new QueryWrapper<RecipeIngredient>().eq("recipe_id", id).select("DISTINCT name, amount")));
        result.put("comments", commentMapper.selectList(new QueryWrapper<Comment>().eq("recipe_id", id).orderByDesc("created_at")));
        
        return result;
    }

    @PostMapping("/comment")
    public String addComment(@RequestBody Map<String, Object> params) {
        Comment comment = new Comment();
        comment.setRecipeId(Long.valueOf(params.get("recipeId").toString()));
        comment.setUserId(Long.valueOf(params.get("userId").toString()));
        comment.setContent(params.get("content").toString());
        comment.setCreatedAt(java.time.LocalDateTime.now());
        commentMapper.insert(comment);
        return "评论发表成功";
    }

    @PostMapping("/like")
    public Map<String, Object> toggleLike(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        if (params.get("isCurrentlyLiked") != null && (Boolean)params.get("isCurrentlyLiked")) {
            response.put("action", "unliked");
            response.put("message", "已取消收藏");
        } else {
            response.put("action", "liked");
            response.put("message", "收藏成功");
        }
        return response;
    }
}
