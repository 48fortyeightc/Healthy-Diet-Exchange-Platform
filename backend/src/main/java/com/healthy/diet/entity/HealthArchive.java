package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("health_archives")
public class HealthArchive {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    
    // 数据库中是 VARBINARY，对应 Java 的 byte[]
    private byte[] weightEncrypted;
    private byte[] heightEncrypted;
    
    private BigDecimal bmi;
    private String allergies;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}



