USE healthy_diet_db;

-- ----------------------------------------------------------
-- 补充互动与业务数据 (让项目看起来更真实)
-- ----------------------------------------------------------

-- 1. 补充健康档案 (演示加密字段和约束)
-- 注意：这里使用模拟的二进制数据，实际应用中由后端加密插入
INSERT INTO health_archives (user_id, weight_encrypted, height_encrypted, bmi, allergies) VALUES 
(1, X'536563726574576569676874', X'536563726574486569676874', 22.5, '无'),
(2, X'536563726574576569676874', X'536563726574486569676874', 24.1, '海鲜过敏'),
(1010, X'536563726574576569676874', X'536563726574486569676874', 21.0, '花生过敏');

-- 2. 补充点赞数据 (演示多对多关系)
INSERT INTO likes (user_id, recipe_id) VALUES 
(1, 10), (1, 11), (1, 12),
(2, 10), (2, 13), (2, 14),
(1010, 15), (1011, 10), (1012, 11);

-- 3. 补充评论数据 (演示树形结构/楼中楼)
INSERT INTO comments (id, user_id, recipe_id, content) VALUES 
(1, 1, 10, '这个面包看起来太棒了，颜值真的高！'),
(2, 2, 10, '我也想试试，感觉步骤很清晰。');

-- 楼中楼回复
INSERT INTO comments (id, user_id, recipe_id, parent_id, content) VALUES 
(3, 1010, 10, 1, '谢谢夸奖，一定要试试看哦！'),
(4, 1, 10, 3, '好的，这周末就安排！');

-- 4. 补充食材共享数据 (演示空间索引 Point)
-- POINT(经度, 纬度)
INSERT INTO ingredient_shares (provider_id, name, quantity, expiry_date, location, status) VALUES 
(1, '多余的低筋面粉', '约1kg', '2026-03-01', ST_GeomFromText('POINT(116.4074 39.9042)'), 'AVAILABLE'),
(2, '自家种的生菜', '3颗', '2025-12-30', ST_GeomFromText('POINT(116.3974 39.9142)'), 'AVAILABLE'),
(1011, '吃不完的土豆', '5个', '2026-01-15', ST_GeomFromText('POINT(116.4174 39.8942)'), 'AVAILABLE');

-- 5. 补充模拟消息
INSERT INTO chat_messages (sender_id, receiver_id, share_id, content) VALUES 
(2, 1, 1, '你好，面粉还在吗？我想用我的生菜换一点。'),
(1, 2, 1, '在的，没问题，怎么交换？');

-- 6. 模拟一些审计日志 (展示 JSON 支持)
INSERT INTO audit_logs (operator_id, action, target_table, target_id, old_value, new_value) VALUES 
(1, 'UPDATE', 'users', 2, '{"status": 1}', '{"status": 0}'),
(1, 'UPDATE', 'users', 2, '{"status": 0}', '{"status": 1}');



