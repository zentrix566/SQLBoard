# SQLBoard

SQLBoard 是一个面向运维/开发的 **数据操作管理平台**，解决日常数据操作的痛点，让你安全高效地完成数据操作：

- ✅ **用户登录认证** - 基础身份认证，未登录无法访问，保障数据安全
- ✅ **即席数据查询修改** - 在线编写执行 SQL，无需登录跳板机，所有操作留痕
- ✅ **常用操作复用** - 将常用的统计、改数、查询保存下来，点击直接执行不用重复写 SQL
- ✅ **生产→测试数据导出** - 导出同时自动完成敏感数据脱敏，直接拿到测试环境用
- ✅ **大数据量支持** - 基于 EasyExcel 流式读写，导出百万行也不 OOM
- ✅ **危险操作多层防护** - 检测到 DROP/TRUNCATE/全表 DELETE 强制高亮警告，生产环境直接拦截高危操作
- ✅ **完整审计日志** - 谁、在什么时候、操作了什么、结果如何，全部记录

## 功能概览

| 功能 | 说明 |
|------|------|
| **数据源管理** | 管理多个数据库连接，区分生产/测试/开发环境 |
| **SQL 执行 - 查询** | 执行 SELECT/SHOW 等只读语句，返回结果集，支持脱敏 |
| **SQL 执行 - 更新** | 执行 INSERT/UPDATE/DELETE 等写操作，需要二次确认防止误操作，返回影响行数 |
| **数据导出** | 导出查询结果到 Excel，支持大数据量和自动脱敏 |
| **常用操作** | 保存常用 SQL，点击直接执行，不用重复编写 |
| **操作历史** | 分页查看所有操作记录，支持筛选，完整审计追踪 |

## 支持的脱敏类型

| 类型 | 示例 | 脱敏后 |
|------|------|--------|
| 中文姓名 | 张三 | 张* |
| 身份证 | 110101199001011234 | 110***********1234 |
| 手机号 | 13800138000 | 138****8000 |
| 邮箱 | example@gmail.com | e******@gmail.com |
| 银行卡 | 622201234567890 | 6222***********890 |
| 地址 | 北京市海淀区中关村大街1号 | 北京市海淀区******** |
| 完全隐藏 | 任意内容 | ****** |

## 技术栈

- **后端**: Java Spring Boot 3.2 + Spring Security + JWT + MyBatis Plus 3.5 + HikariCP + Alipay EasyExcel 3.3 + Hutool
- **前端**: Vue 3 + Vite + Element Plus
- **数据库**: 支持 MySQL、PostgreSQL 动态连接

## 项目结构

```
SQLBoard/
├── backend/          # Java Spring Boot 后端
│   └── src/main/java/com/sqlboard/
│       ├── config/   # CORS/MyBatis Plus/Security 配置
│       ├── controller/ # REST API 控制器（Auth/Sql/Connection/Operation/History）
│       ├── service/    # 业务逻辑层
│       ├── dto/        # 请求响应 DTO
│       ├── entity/     # 数据库实体 (User/Connection/SavedOperation/OperationHistory)
│       ├── mapper/     # MyBatis Plus Mapper
│       └── util/      # 工具类 (desensitization/JWT)
├── frontend/         # Vue 3 前端
│   ├── src/
│   │   ├── api/      # API 客户端 (auth/sql/connection/operation/history)
│   │   ├── views/    # 页面组件 (Login/SqlExecute/ConnectionList/SavedOperations/OperationHistory)
│   │   └── router/   # 路由配置
├── backend/src/main/resources/
│   └── schema.sql    # 数据库初始化脚本
└── README.md
```

## 快速开始

### 1. 初始化数据库

创建数据库 `sqlboard`，然后执行 `backend/src/main/resources/schema.sql` 初始化表结构：

```bash
mysql -u root -p < backend/src/main/resources/schema.sql
```

默认管理员账号：
- **用户名**: `admin`
- **密码**: `admin123`

### 2. 修改配置

修改 `backend/src/main/resources/application.yml` 中的数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sqlboard?...
    username: root
    password: your-password
```

### 3. 启动后端

```bash
cd backend
mvn clean spring-boot:run
```

后端服务运行在 `http://localhost:8081`

### 4. 启动前端

```bash
cd frontend
npm install
npm run dev
```

前端访问地址 `http://localhost:3000`

## 界面预览

```
┌─────────────┬─────────────────────────────────────────────┐
│ SQL执行     │                                             │
│ 数据源管理  │   [ SQL 编辑区 ]                             │
│ 常用操作   │   [ 执行按钮：查询 / 执行更新 / 导出 ]         │
│ 操作历史   │                                             │
├─────────────┼─────────────────────────────────────────────┤
│             │ 卡片式布局，操作清晰，响应式表格展示结果       │
└─────────────┴─────────────────────────────────────────────┘
```

## 使用流程

1. **添加数据源** → 填写连接信息
2. **在 SQL 执行页编写 SQL** → 选择查询/更新
3. **如果需要经常使用** → 点击"保存为常用操作"
4. **以后在常用操作页** → 点击"使用"直接跳转到 SQL 执行页并填好 SQL
5. **导出数据时** → 勾选"需要脱敏"，配置哪些列需要脱敏、用什么规则
6. **所有操作都会记录** → 在操作历史页可以查看追溯

## 安全设计

- ✅ **用户认证** - 需要登录才能访问，未登录自动跳登录页
- ✅ **写操作二次确认** - 执行更新/删除需要手动确认才能执行
- ✅ **高危操作强警告** - DROP/TRUNCATE/全表DELETE 检测到后显示红色警告
- ✅ **生产环境高危拦截** - 生产环境连接直接拒绝执行 DROP/TRUNCATE，从根本防止删库
- ✅ **自动脱敏** - 导出数据时自动对敏感字段脱敏
- ✅ **完整审计** - 所有操作全部记录，谁操作了什么一目了然

## 设计亮点

- **安全** - 多层安全防护防止误操作，敏感数据自动脱敏，完整审计日志
- **高效** - 常用操作一键执行，不用重复写 SQL
- **稳定** - 流式读写处理大数据量，不会 OOM
- **美观** - 基于 Element Plus 的现代化界面，操作直观

## 许可证

MIT

