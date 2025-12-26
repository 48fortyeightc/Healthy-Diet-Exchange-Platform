# 🥗 健康饮食交流平台 (Healthy Diet Exchange Platform)

[![Language](https://img.shields.io/badge/Language-Java/Vue/SQL-blue.svg)]()
[![Framework](https://img.shields.io/badge/Framework-SpringBoot3/Vue3-green.svg)]()
[![Database](https://img.shields.io/badge/Database-MySQL8.0-orange.svg)]()

本项目是一款模仿“豆果美食”风格的健康饮食社区平台，旨在作为**数据库课程大作业**，全面展示数据库建模、高级特性应用及前后端交互逻辑。

## 🌟 项目亮点
- **全方位数据库特性展示**：包含视图(View)、触发器(Trigger)、存储过程(Procedure)、事务处理(Transaction)、字段加密及 CHECK 约束。
- **现代化技术栈**：后端采用 Spring Boot 3 + MyBatis-Plus，前端采用 Vue 3 + Element Plus，支持响应式布局。
- **完善的业务闭环**：涵盖用户账号、食谱发布、健康文章、邻里食材共享、社区互动（点赞评论）及管理员审核模块。

---

## 📖 详细文档
为了方便阅读与理解，本项目将技术细节拆分为两个独立文档：

1.  [**🛠️ 技术实现说明**](./docs/TECH_SPEC.md)：讲解后端架构、前端逻辑及核心功能实现。
2.  [**🗄️ 数据库逻辑说明**](./docs/DATABASE_DESIGN.md)：深度解析 E-R 模型、高级 SQL 特性及安全性设计。

---

## 🚀 快速开始

### 1. 环境准备
- MySQL 8.0+
- JDK 17+
- Node.js 16+

### 2. 数据库部署
执行 `database/` 目录下的脚本（按顺序）：
1.  `schema.sql`：创建表结构、视图、触发器和存储过程。
2.  `import_data.sql`：导入初始采集的真实菜谱数据。
3.  `mock_interactions.sql`：生成模拟的互动、审计和共享数据。

### 3. 后端启动
```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库密码
mvn spring-boot:run
```

### 4. 前端启动
```bash
cd frontend
npm install
npm run dev
```

---

## 📂 目录结构预览
```text
├── backend/            # Spring Boot 后端源码
├── frontend/           # Vue 3 前端源码
├── database/           # 全量 SQL 脚本
├── docs/               # 详细说明文档
└── process_csv.py      # 原始数据处理脚本
```

## 🛠️ 管理员权限
- **测试账号**：admin
- **测试密码**：admin_hash (演示环境下直接匹配)

---

## 🔗 GitHub 仓库
如果你喜欢这个项目，欢迎 Star！⭐
[Healthy-Diet-Exchange-Platform](git@github.com:48fortyeightc/Healthy-Diet-Exchange-Platform.git)
