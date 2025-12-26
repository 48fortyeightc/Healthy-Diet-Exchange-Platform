package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.healthy.diet.entity.Comment;
import com.healthy.diet.mapper.CommentMapper;
import com.healthy.diet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community")
public class CommunityController {

    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取所有评论（展示社区互动）
     */
    @GetMapping("/comments")
    public List<Map<String, Object>> getComments() {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("created_at");
        List<Comment> comments = commentMapper.selectList(wrapper);
        
        return comments.stream().map(comment -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", comment.getId());
            map.put("userId", comment.getUserId());
            map.put("recipeId", comment.getRecipeId());
            map.put("content", comment.getContent());
            map.put("createdAt", comment.getCreatedAt());
            map.put("parentId", comment.getParentId());
            return map;
        }).collect(Collectors.toList());
    }
}



