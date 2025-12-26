package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.healthy.diet.entity.*;
import com.healthy.diet.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private UserProfileMapper userProfileMapper;
    
    @Autowired
    private HealthArchiveMapper healthArchiveMapper;

    @Autowired
    private RecipeDetailMapper recipeDetailMapper;

    @Autowired
    private PostMapper postMapper;

    @GetMapping("/favorites")
    public Map<String, Object> getMyFavorites(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        // 获取收藏的食谱 (利用 IN 子查询演示)
        List<RecipeDetail> recipes = recipeDetailMapper.selectList(new QueryWrapper<RecipeDetail>()
                .inSql("id", "SELECT recipe_id FROM likes WHERE user_id = " + userId));
        
        // 获取收藏的文章
        List<Post> posts = postMapper.selectList(new QueryWrapper<Post>()
                .inSql("id", "SELECT post_id FROM post_likes WHERE user_id = " + userId));
        
        result.put("recipes", recipes);
        result.put("posts", posts);
        return result;
    }

    @GetMapping("/{userId}")
    public Map<String, Object> getProfile(@PathVariable Long userId) {
        // ... 原有逻辑保持不变 ...
        Map<String, Object> result = new HashMap<>();
        User user = userMapper.selectById(userId);
        result.put("user", user);
        QueryWrapper<UserProfile> profileWrapper = new QueryWrapper<>();
        profileWrapper.eq("user_id", userId);
        UserProfile profile = userProfileMapper.selectOne(profileWrapper);
        result.put("profile", profile);
        QueryWrapper<HealthArchive> healthWrapper = new QueryWrapper<>();
        healthWrapper.eq("user_id", userId);
        HealthArchive health = healthArchiveMapper.selectOne(healthWrapper);
        result.put("health", health);
        result.put("displayWeight", "70.5");
        result.put("displayHeight", "175.0");
        return result;
    }

    @PostMapping("/updateHealth")
    public String updateHealth(@RequestBody Map<String, Object> params) {
        // ... 原有逻辑 ...
        Long userId = Long.valueOf(params.get("userId").toString());
        Double bmi = Double.valueOf(params.get("bmi").toString());
        String allergies = params.get("allergies").toString();
        QueryWrapper<HealthArchive> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        HealthArchive archive = healthArchiveMapper.selectOne(wrapper);
        if (archive != null) {
            archive.setBmi(new java.math.BigDecimal(bmi));
            archive.setAllergies(allergies);
            try {
                healthArchiveMapper.updateById(archive);
                return "更新成功";
            } catch (Exception e) {
                return "更新失败：数据库 CHECK 约束校验未通过！";
            }
        }
        return "用户不存在";
    }

    @PostMapping("/updateProfile")
    public String updateProfile(@RequestBody Map<String, Object> params) {
        Long userId = Long.valueOf(params.get("userId").toString());
        String nickname = params.get("nickname").toString();
        String gender = params.get("gender").toString();
        String bio = params.get("bio").toString();

        QueryWrapper<UserProfile> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        UserProfile profile = userProfileMapper.selectOne(wrapper);

        if (profile != null) {
            profile.setNickname(nickname);
            profile.setGender(gender);
            profile.setBio(bio);
            userProfileMapper.updateById(profile);
            return "资料更新成功";
        }
        return "用户不存在";
    }
}

