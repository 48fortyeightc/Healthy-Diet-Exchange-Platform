package com.healthy.diet.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.healthy.diet.entity.AuditLog;
import com.healthy.diet.entity.Recipe;
import com.healthy.diet.entity.User;
import com.healthy.diet.mapper.AuditLogMapper;
import com.healthy.diet.mapper.RecipeMapper;
import com.healthy.diet.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private RecipeMapper recipeMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AuditLogMapper auditLogMapper;

    /**
     * 获取系统所有用户（用户管理）
     */
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userMapper.selectList(new QueryWrapper<User>().orderByDesc("created_at"));
    }

    /**
     * 更新用户状态（禁用/启用）
     */
    @PostMapping("/user/status")
    public String updateUserStatus(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        Integer status = Integer.valueOf(params.get("status").toString()); // 1: 正常, 0: 禁用
        
        User user = userMapper.selectById(id);
        if (user != null) {
            user.setStatus(status);
            userMapper.updateById(user);
            return "用户状态已更新为: " + (status == 1 ? "正常" : "禁用");
        }
        return "用户不存在";
    }

    /**
     * 获取所有内容供审核（食谱管理）
     */
    @GetMapping("/recipes")
    public List<Recipe> getAllRecipes() {
        return recipeMapper.selectList(new QueryWrapper<Recipe>().orderByDesc("created_at"));
    }

    /**
     * 审核操作：下架或恢复内容
     */
    @PostMapping("/recipe/audit")
    public String auditRecipe(@RequestBody Map<String, Object> params) {
        Long id = Long.valueOf(params.get("id").toString());
        String status = params.get("status").toString(); // PUBLISHED or REJECTED
        
        Recipe recipe = recipeMapper.selectById(id);
        if (recipe != null) {
            String oldStatus = recipe.getStatus();
            recipe.setStatus(status);
            recipeMapper.updateById(recipe);
            
            // 手动记录审核日志（演示JSON存储）
            AuditLog log = new AuditLog();
            log.setAction("AUDIT_CONTENT");
            log.setTargetTable("recipes");
            log.setTargetId(id);
            log.setOldValue("{\"status\": \"" + oldStatus + "\"}");
            log.setNewValue("{\"status\": \"" + status + "\"}");
            auditLogMapper.insert(log);
            
            return "操作成功，已更新状态为: " + status;
        }
        return "食谱不存在";
    }

    /**
     * 获取系统审计日志（展示触发器和手动日志）
     */
    @GetMapping("/audit-logs")
    public List<AuditLog> getAuditLogs() {
        return auditLogMapper.selectList(new QueryWrapper<AuditLog>().orderByDesc("created_at"));
    }
}

