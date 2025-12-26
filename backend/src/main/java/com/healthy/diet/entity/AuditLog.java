package com.healthy.diet.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("audit_logs")
public class AuditLog {
    @TableId
    private Long id;
    private Long operatorId;
    private String action;
    private String targetTable;
    private Long targetId;
    private String oldValue;
    private String newValue;
    private String ipAddress;
    private LocalDateTime createdAt;
}

