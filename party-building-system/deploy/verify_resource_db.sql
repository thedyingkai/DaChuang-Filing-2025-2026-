-- 在 Navicat 中连接到 application.properties 里配置的同一个库（默认 djpt@远程主机）
-- 资源上传写入 resource_view，不会写入 activity 表

SHOW TABLES LIKE '%resource%';
SHOW TABLES LIKE '%activity%';

-- 应有 resource_view（或同名可插入视图），不应只在 activity 表里找数据
SELECT COUNT(*) AS resource_cnt FROM resource_view;
SELECT id, uid, name, `time` FROM activity_view ORDER BY id LIMIT 20;

-- 未分类活动（上传默认依赖 id=-1），若不存在需执行 init_data_mysql.sql 中 activity 段
SELECT * FROM activity_view WHERE id = -1;
