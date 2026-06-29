-- 资源上传外键依赖：resource.acid -> activity.acid，resource.uid -> user1.uid
-- 若上传报「SQL 插入数据错误」或外键 1452，在 djpt 库执行本脚本后重试

SET NAMES utf8mb4;

-- 未分类活动（上传默认 acid=-1），已存在则跳过
INSERT INTO activity (uid, acid, ac_name, ac_time, ac_type, ac_content, cover_image)
SELECT NULL, -1, '（未分类资源）', NULL, NULL, NULL, '/file/download/dsjtjt.jpg'
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM activity WHERE acid = -1);

-- 校验
SELECT acid AS activity_id, ac_name FROM activity WHERE acid IN (-1, 5, 6);
SELECT COUNT(*) AS resource_cnt FROM resource;
