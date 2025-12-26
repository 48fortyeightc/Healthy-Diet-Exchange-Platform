package com.healthy.diet.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.healthy.diet.entity.IngredientShare;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface IngredientShareMapper extends BaseMapper<IngredientShare> {
    
    // 使用原生SQL查询附近的食材（展示空间索引功能）
    @Select("SELECT id, provider_id, name, quantity, expiry_date, status, " +
            "ST_AsText(location) as location_text, " +
            "ST_Distance(location, ST_GeomFromText(#{centerPoint})) as distance " +
            "FROM ingredient_shares " +
            "WHERE status = 'AVAILABLE' " +
            "ORDER BY distance LIMIT 20")
    List<Map<String, Object>> findNearby(String centerPoint);
}



