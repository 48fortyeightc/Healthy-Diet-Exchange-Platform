# 🛠️ 健康饮食交流平台 - 技术实现说明文档

本项目采用主流的前后端分离架构，旨在通过一个完整的 Web 应用展示数据库的高级应用与业务逻辑处理能力。

## 1. 技术栈概览

### 后端 (Spring Boot 3.x)
- **核心框架**：Spring Boot, Spring Web
- **ORM 框架**：MyBatis-Plus (实现高效的 CRUD 与 分页)
- **安全校验**：Spring Security (配置 CORS 与 基础访问控制)
- **数据库连接**：MySQL Connector/J, Druid 线程池
- **工具类**：Lombok (简化代码), Jackson (JSON 处理)

### 前端 (Vue 3.x)
- **核心框架**：Vue 3 (Composition API)
- **构建工具**：Vite
- **UI 组件库**：Element Plus (提供响应式布局与现代化 UI)
- **路由管理**：Vue Router
- **网络请求**：Axios

---

## 2. 核心功能代码解析

### 2.1 统一作品中心 (多态内容管理)
为了在“我的作品”中同时管理食谱（Recipe）和文章（Post），后端控制器通过 `userId` 分别从两张表中提取数据，前端通过 Element Plus 的 `el-tabs` 实现无缝切换。
- **关键代码**：`PostController.java` 和 `RecipeController.java`
- **实现逻辑**：前端调用多个接口后使用 `Promise.all` 合并数据，提升加载性能。

### 2.2 响应式分页系统
为了处理大规模数据，本项目在后端配置了 `MybatisPlusInterceptor` 分页插件。
- **后端实现**：通过 `Page<T>` 对象接收前端传参，自动生成 `LIMIT` 分页 SQL。
- **前端实现**：`el-pagination` 组件与 API 联动，实现点击页码实时异步加载数据。

### 2.3 收藏与互动逻辑
- **收藏状态回显**：在进入详情页时，后端会根据 `userId` 检查 `likes` 或 `post_likes` 表，返回 `isLiked` 布尔值，前端据此渲染高亮图标。
- **实时评论刷新**：发表评论后，前端通过触发列表重新加载，实现“伪实时”的评论展示效果。

### 2.4 食材共享区域检索
本项目将复杂的空间地理坐标优化为更具实用性的“行政区划检索”。
- **查询逻辑**：`ShareController` 接收 `region` 参数，执行精确匹配过滤。

---

## 3. 安全性实现
- **SQL 注入防护**：全量使用 MyBatis 预编译参数 (`#{}`)。
- **CORS 跨域配置**：通过 `WebConfig.java` 严格限制前端来源，保障接口安全。
- **数据脱敏**：在数据库视图层利用 `CONCAT` 和 `LEFT/RIGHT` 函数对用户手机号进行掩码处理，确保敏感隐私不泄露。

---

## 4. 运行与构建
- **后端编译**：使用 Maven 进行编译构建，生成 JAR 包。
- **前端打包**：使用 `npm run build` 生成静态资源，部署至 Nginx 或直接由 Spring Boot 托管。

