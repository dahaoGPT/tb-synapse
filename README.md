# TB-Synapse 后台管理系统

基于 **Spring Boot + Vue 3** 的前后端分离后台管理系统，提供用户、角色、菜单、部门等基础功能管理。

## 技术栈

| 层级 | 技术 | 版本 |
|------|------|------|
| **后端框架** | Spring Boot | 2.7.18 |
| **安全认证** | Spring Security + JWT | - |
| **ORM** | MyBatis-Plus | 3.5.5 |
| **数据库** | PostgreSQL | - |
| **前端框架** | Vue 3 | 3.4+ |
| **构建工具** | Vite | 5.4+ |
| **UI 组件库** | Element Plus | 2.7+ |
| **状态管理** | Pinia | 2.1+ |
| **HTTP 客户端** | Axios | 1.7+ |

## 项目结构

```
tb-synapse/
├── admin-server/                # 后端服务 (Java)
│   ├── src/main/java/com/admin/
│   │   ├── AdminApplication.java    # 启动类
│   │   ├── common/                  # 公共模块（响应封装、工具类等）
│   │   ├── config/                  # 配置类
│   │   ├── controller/              # 控制器层
│   │   ├── entity/                  # 实体类
│   │   ├── mapper/                  # 数据访问层
│   │   ├── security/                # 安全认证模块
│   │   └── service/                 # 业务逻辑层
│   ├── src/main/resources/
│   │   ├── application.yml          # 应用配置
│   │   └── mapper/                  # MyBatis XML 映射文件
│   └── pom.xml
├── admin-ui/                    # 前端应用 (Vue 3)
│   ├── src/
│   │   ├── api/                     # 接口请求
│   │   ├── components/              # 公共组件
│   │   ├── layout/                  # 布局组件
│   │   ├── router/                  # 路由配置
│   │   ├── store/                   # Pinia 状态管理
│   │   ├── styles/                  # 全局样式
│   │   ├── utils/                   # 工具函数
│   │   ├── views/                   # 页面视图
│   │   │   ├── dashboard/           # 首页
│   │   │   ├── login/               # 登录页
│   │   │   └── system/              # 系统管理
│   │   │       ├── user/            # 用户管理
│   │   │       ├── role/            # 角色管理
│   │   │       ├── menu/            # 菜单管理
│   │   │       └── dept/            # 部门管理
│   │   ├── permission.js            # 路由权限控制
│   │   └── main.js                  # 入口文件
│   ├── vite.config.js
│   └── package.json
├── sql/                         # 数据库脚本
│   └── init.sql                     # 初始化建表与数据
├── .gitignore
└── README.md
```

## 功能模块

- 🔐 **登录认证** — JWT Token 机制，支持权限指令 `v-hasPermi`
- 👤 **用户管理** — 用户增删改查、角色分配
- 🎭 **角色管理** — 角色权限配置
- 📋 **菜单管理** — 动态菜单与权限标识
- 🏢 **部门管理** — 组织架构树形管理
- 📊 **首页仪表盘** — 数据概览

## 快速开始

### 环境要求

- JDK 1.8+
- Maven 3.6+
- Node.js 18+
- PostgreSQL 12+

### 1. 初始化数据库

```bash
# 创建数据库
createdb tb_synapse

# 执行初始化脚本
psql -d tb_synapse -f sql/init.sql
```

### 2. 启动后端

```bash
cd admin-server
mvn spring-boot:run
```

后端服务将运行在 `http://localhost:8080`

### 3. 启动前端

```bash
cd admin-ui
npm install
npm run dev
```

前端开发服务器将运行在 `http://localhost:3000`，API 请求会自动代理到后端 8080 端口。

## 开发说明

- 前端通过 Vite 代理将 `/api` 前缀的请求转发到后端 `http://localhost:8080`
- 数据库连接配置位于 `admin-server/src/main/resources/application.yml`
- 权限控制采用 RBAC 模型，支持菜单级和按钮级权限
