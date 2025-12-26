package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.healthy.diet.entity.IngredientShare;
import com.healthy.diet.mapper.IngredientShareMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/shares")
public class ShareController {

    @Autowired
    private IngredientShareMapper ingredientShareMapper;

    @GetMapping("/list")
    public List<IngredientShare> list() {
        QueryWrapper<IngredientShare> wrapper = new QueryWrapper<>();
        wrapper.eq("status", "AVAILABLE").orderByDesc("created_at");
        return ingredientShareMapper.selectList(wrapper);
    }

    /**
     * 按区域查询共享食材
     */
    @GetMapping("/nearby")
    public List<IngredientShare> findNearby(@RequestParam String region) {
        QueryWrapper<IngredientShare> wrapper = new QueryWrapper<>();
        wrapper.eq("region", region).eq("status", "AVAILABLE").orderByDesc("created_at");
        return ingredientShareMapper.selectList(wrapper);
    }

    /**
     * 发布食材共享
     */
    @PostMapping("/publish")
    public String publishShare(@RequestBody IngredientShare share) {
        if (share.getId() != null) {
            ingredientShareMapper.updateById(share);
        } else {
            share.setCreatedAt(LocalDateTime.now());
            ingredientShareMapper.insert(share);
        }
        return "食材共享发布成功";
    }
}
