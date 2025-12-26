-- ==========================================================
-- 健康饮食交流平台 (豆果美食风格) - 数据库设计
-- 特性：规范化、安全性加密、触发器审计、存储过程、视图、空间索引
-- ==========================================================

CREATE DATABASE IF NOT EXISTS healthy_diet_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE healthy_diet_db;

-- ----------------------------------------------------------
-- 1. 用户与权限模块
-- ----------------------------------------------------------

-- 用户账户表 (核心：逻辑删除, 状态控制)
CREATE TABLE IF NOT EXISTS `users` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password_hash` VARCHAR(255) NOT NULL COMMENT '哈希加密密码',
    `email` VARCHAR(100) UNIQUE COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号(原文)',
    `role` ENUM('USER', 'ADMIN') DEFAULT 'USER' COMMENT '角色',
    `status` TINYINT DEFAULT 1 COMMENT '1:正常, 0:禁用',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除标识',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (`username`),
    INDEX idx_is_deleted (`is_deleted`)
) ENGINE=InnoDB COMMENT='用户账户表';

-- 用户资料表
CREATE TABLE IF NOT EXISTS `user_profiles` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `nickname` VARCHAR(50) DEFAULT '新用户',
    `avatar_url` VARCHAR(255),
    `gender` ENUM('M', 'F', 'S') DEFAULT 'S' COMMENT 'M:男, F:女, S:保密',
    `bio` TEXT,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='用户资料表';

-- 健康档案表 (核心：字段加密存储, CHECK约束)
CREATE TABLE IF NOT EXISTS `health_archives` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL UNIQUE,
    -- 下面两个字段在业务层使用 AES_ENCRYPT 加密存储为二进制
    `weight_encrypted` VARBINARY(255) COMMENT '加密后的体重',
    `height_encrypted` VARBINARY(255) COMMENT '加密后的身高',
    `bmi` DECIMAL(4,2) COMMENT '计算得出的BMI',
    `allergies` TEXT COMMENT '过敏史',
    `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT `chk_bmi` CHECK (`bmi` > 0 AND `bmi` < 100),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='健康档案表';

-- ----------------------------------------------------------
-- 2. 食谱内容模块
-- ----------------------------------------------------------

-- 食谱主表 (核心：全文索引, 状态审核)
CREATE TABLE IF NOT EXISTS `recipes` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `author_id` BIGINT UNSIGNED NOT NULL,
    `title` VARCHAR(100) NOT NULL,
    `description` TEXT,
    `cover_image` VARCHAR(255),
    `difficulty` ENUM('EASY', 'NORMAL', 'HARD') DEFAULT 'NORMAL',
    `cooking_time` INT COMMENT '分钟',
    `status` ENUM('PENDING', 'PUBLISHED', 'REJECTED') DEFAULT 'PENDING',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FULLTEXT INDEX ft_title_desc (`title`, `description`),
    FOREIGN KEY (`author_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB COMMENT='食谱主表';

-- 烹饪步骤表
CREATE TABLE IF NOT EXISTS `recipe_steps` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `recipe_id` BIGINT UNSIGNED NOT NULL,
    `step_number` INT NOT NULL,
    `instruction` TEXT NOT NULL,
    `image_url` VARCHAR(255),
    FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='烹饪步骤表';

-- 食谱食材表 (多对多关系简化版)
CREATE TABLE IF NOT EXISTS `recipe_ingredients` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `recipe_id` BIGINT UNSIGNED NOT NULL,
    `name` VARCHAR(50) NOT NULL,
    `amount` VARCHAR(50) NOT NULL,
    FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='食谱食材表';

-- ----------------------------------------------------------
-- 3. 社区互动模块
-- ----------------------------------------------------------

-- 评论表 (核心：层级结构)
CREATE TABLE IF NOT EXISTS `comments` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `user_id` BIGINT UNSIGNED NOT NULL,
    `recipe_id` BIGINT UNSIGNED NOT NULL,
    `parent_id` BIGINT UNSIGNED DEFAULT NULL COMMENT '父评论ID,用于回复',
    `content` TEXT NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`parent_id`) REFERENCES `comments`(`id`) ON DELETE SET NULL
) ENGINE=InnoDB COMMENT='评论表';

-- 点赞表 (核心：复合主键)
CREATE TABLE IF NOT EXISTS `likes` (
    `user_id` BIGINT UNSIGNED NOT NULL,
    `recipe_id` BIGINT UNSIGNED NOT NULL,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`user_id`, `recipe_id`),
    FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`recipe_id`) REFERENCES `recipes`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB COMMENT='点赞表';

-- ----------------------------------------------------------
-- 4. 食材共享模块
-- ----------------------------------------------------------

-- 食材共享发布表 (核心：空间索引 Point)
CREATE TABLE IF NOT EXISTS `ingredient_shares` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `provider_id` BIGINT UNSIGNED NOT NULL,
    `name` VARCHAR(100) NOT NULL,
    `quantity` VARCHAR(50),
    `expiry_date` DATE,
    `location` POINT NOT NULL COMMENT '经纬度坐标',
    `status` ENUM('AVAILABLE', 'EXCHANGED', 'EXPIRED') DEFAULT 'AVAILABLE',
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    SPATIAL INDEX idx_location (`location`),
    FOREIGN KEY (`provider_id`) REFERENCES `users`(`id`)
) ENGINE=InnoDB COMMENT='食材共享发布表';

-- 即时通讯消息表
CREATE TABLE IF NOT EXISTS `chat_messages` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `sender_id` BIGINT UNSIGNED NOT NULL,
    `receiver_id` BIGINT UNSIGNED NOT NULL,
    `share_id` BIGINT UNSIGNED NOT NULL,
    `content` TEXT NOT NULL,
    `is_read` TINYINT DEFAULT 0,
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (`sender_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`receiver_id`) REFERENCES `users`(`id`),
    FOREIGN KEY (`share_id`) REFERENCES `ingredient_shares`(`id`)
) ENGINE=InnoDB COMMENT='即时通讯消息表';

-- ----------------------------------------------------------
-- 5. 安全审计与高级特性
-- ----------------------------------------------------------

-- 操作审计日志表 (核心：JSON存储)
CREATE TABLE IF NOT EXISTS `audit_logs` (
    `id` BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    `operator_id` BIGINT UNSIGNED COMMENT '操作人ID',
    `action` VARCHAR(50) NOT NULL COMMENT '操作类型: INSERT, UPDATE, DELETE',
    `target_table` VARCHAR(50) NOT NULL COMMENT '目标表',
    `target_id` BIGINT UNSIGNED NOT NULL COMMENT '目标行ID',
    `old_value` JSON COMMENT '旧数据',
    `new_value` JSON COMMENT '新数据',
    `ip_address` VARCHAR(45),
    `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB COMMENT='操作审计日志表';

-- 视图：食谱详情脱敏展示 (核心：安全脱敏, 数据聚合)
CREATE OR REPLACE VIEW `v_recipe_details` AS
SELECT 
    r.id,
    r.title,
    r.cover_image,
    r.difficulty,
    r.cooking_time,
    p.nickname AS author_name,
    -- 对作者手机号进行脱敏处理：138****5678
    CONCAT(LEFT(u.phone, 3), '****', RIGHT(u.phone, 4)) AS author_phone_masked,
    (SELECT COUNT(*) FROM likes l WHERE l.recipe_id = r.id) AS like_count,
    (SELECT COUNT(*) FROM comments c WHERE c.recipe_id = r.id) AS comment_count
FROM recipes r
JOIN users u ON r.author_id = u.id
LEFT JOIN user_profiles p ON u.id = p.user_id
WHERE r.status = 'PUBLISHED' AND u.is_deleted = 0;

-- 触发器：审计评论删除操作 (核心：自动化安全追踪)
DELIMITER //
CREATE TRIGGER tr_audit_comment_delete
AFTER DELETE ON comments
FOR EACH ROW
BEGIN
    INSERT INTO audit_logs (operator_id, action, target_table, target_id, old_value)
    VALUES (NULL, 'DELETE', 'comments', OLD.id, JSON_OBJECT('content', OLD.content, 'user_id', OLD.user_id));
END//
DELIMITER ;

-- 存储过程：安全注册用户 (核心：原子性业务逻辑)
DELIMITER //
CREATE PROCEDURE sp_register_user(
    IN p_username VARCHAR(50),
    IN p_password_hash VARCHAR(255),
    IN p_email VARCHAR(100),
    IN p_phone VARCHAR(20),
    IN p_nickname VARCHAR(50)
)
BEGIN
    DECLARE v_user_id BIGINT UNSIGNED;
    
    -- 开启事务
    START TRANSACTION;
    
    -- 1. 插入账户
    INSERT INTO users (username, password_hash, email, phone) 
    VALUES (p_username, p_password_hash, p_email, p_phone);
    
    SET v_user_id = LAST_INSERT_ID();
    
    -- 2. 初始化资料
    INSERT INTO user_profiles (user_id, nickname) 
    VALUES (v_user_id, p_nickname);
    
    -- 3. 初始化空白健康档案
    INSERT INTO health_archives (user_id) VALUES (v_user_id);
    
    COMMIT;
END//
DELIMITER ;

-- 初始化部分测试数据
INSERT INTO users (username, password_hash, email, phone, role) VALUES 
('admin', 'admin_hash', 'admin@healthy.com', '13812345678', 'ADMIN'),
('testuser', 'user_hash', 'user@healthy.com', '13987654321', 'USER');

INSERT INTO user_profiles (user_id, nickname, bio) VALUES 
(1, '系统管理员', '负责平台维护与审核'),
(2, '美食达人', '喜欢分享健康食谱');

INSERT INTO recipes (author_id, title, description, difficulty, cooking_time, status) VALUES 
(2, '低脂鸡胸肉沙拉', '一份简单又营养的减脂午餐。', 'EASY', 15, 'PUBLISHED'),
(2, '全麦牛油果吐司', '开启活力满满的一天。', 'EASY', 10, 'PUBLISHED');



