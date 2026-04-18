-- =============================================
-- 后台管理系统 - 数据库初始化脚本
-- 技术栈: Spring Boot + MyBatis-Plus + PostgreSQL 16
-- =============================================

-- 创建数据库（如需手动创建可取消注释）
-- CREATE DATABASE tb_synapse ENCODING 'UTF8';
-- \c tb_synapse;

-- ----------------------------
-- 1. 用户表
-- ----------------------------
DROP TABLE IF EXISTS sys_user CASCADE;
CREATE TABLE sys_user (
  id          BIGSERIAL    PRIMARY KEY,
  username    VARCHAR(50)  NOT NULL,
  password    VARCHAR(200) NOT NULL,
  nickname    VARCHAR(50)  DEFAULT '',
  email       VARCHAR(100) DEFAULT '',
  phone       VARCHAR(20)  DEFAULT '',
  avatar      VARCHAR(500) DEFAULT '',
  dept_id     BIGINT       DEFAULT NULL,
  status      SMALLINT     DEFAULT 1,
  data_scope  SMALLINT     DEFAULT 1,
  del_flag    SMALLINT     DEFAULT 0,
  login_ip    VARCHAR(50)  DEFAULT '',
  login_time  TIMESTAMP    DEFAULT NULL,
  create_by   VARCHAR(50)  DEFAULT '',
  create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  update_by   VARCHAR(50)  DEFAULT '',
  update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  remark      VARCHAR(500) DEFAULT ''
);

COMMENT ON TABLE  sys_user IS '用户表';
COMMENT ON COLUMN sys_user.id IS '用户ID';
COMMENT ON COLUMN sys_user.username IS '用户名';
COMMENT ON COLUMN sys_user.password IS '密码(BCrypt加密)';
COMMENT ON COLUMN sys_user.nickname IS '昵称';
COMMENT ON COLUMN sys_user.email IS '邮箱';
COMMENT ON COLUMN sys_user.phone IS '手机号';
COMMENT ON COLUMN sys_user.avatar IS '头像URL';
COMMENT ON COLUMN sys_user.dept_id IS '部门ID';
COMMENT ON COLUMN sys_user.status IS '状态(0禁用 1启用)';
COMMENT ON COLUMN sys_user.data_scope IS '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)';
COMMENT ON COLUMN sys_user.del_flag IS '删除标志(0正常 1已删除)';
COMMENT ON COLUMN sys_user.login_ip IS '最后登录IP';
COMMENT ON COLUMN sys_user.login_time IS '最后登录时间';
COMMENT ON COLUMN sys_user.create_by IS '创建者';
COMMENT ON COLUMN sys_user.create_time IS '创建时间';
COMMENT ON COLUMN sys_user.update_by IS '更新者';
COMMENT ON COLUMN sys_user.update_time IS '更新时间';
COMMENT ON COLUMN sys_user.remark IS '备注';

CREATE UNIQUE INDEX uk_username ON sys_user (username);
CREATE INDEX idx_user_phone ON sys_user (phone);
CREATE INDEX idx_user_status ON sys_user (status);

-- ----------------------------
-- 2. 角色表
-- ----------------------------
DROP TABLE IF EXISTS sys_role CASCADE;
CREATE TABLE sys_role (
  id          BIGSERIAL    PRIMARY KEY,
  role_name   VARCHAR(50)  NOT NULL,
  role_code   VARCHAR(50)  NOT NULL,
  sort_order  INT          DEFAULT 0,
  status      SMALLINT     DEFAULT 1,
  data_scope  SMALLINT     DEFAULT 1,
  del_flag    SMALLINT     DEFAULT 0,
  create_by   VARCHAR(50)  DEFAULT '',
  create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  update_by   VARCHAR(50)  DEFAULT '',
  update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  remark      VARCHAR(500) DEFAULT ''
);

COMMENT ON TABLE  sys_role IS '角色表';
COMMENT ON COLUMN sys_role.id IS '角色ID';
COMMENT ON COLUMN sys_role.role_name IS '角色名称';
COMMENT ON COLUMN sys_role.role_code IS '角色编码';
COMMENT ON COLUMN sys_role.sort_order IS '排序';
COMMENT ON COLUMN sys_role.status IS '状态(0禁用 1启用)';
COMMENT ON COLUMN sys_role.data_scope IS '数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人)';
COMMENT ON COLUMN sys_role.del_flag IS '删除标志(0正常 1已删除)';
COMMENT ON COLUMN sys_role.create_by IS '创建者';
COMMENT ON COLUMN sys_role.create_time IS '创建时间';
COMMENT ON COLUMN sys_role.update_by IS '更新者';
COMMENT ON COLUMN sys_role.update_time IS '更新时间';
COMMENT ON COLUMN sys_role.remark IS '备注';

CREATE UNIQUE INDEX uk_role_code ON sys_role (role_code);

-- ----------------------------
-- 3. 菜单表 (含按钮权限)
-- ----------------------------
DROP TABLE IF EXISTS sys_menu CASCADE;
CREATE TABLE sys_menu (
  id          BIGSERIAL    PRIMARY KEY,
  parent_id   BIGINT       DEFAULT 0,
  menu_name   VARCHAR(50)  NOT NULL,
  menu_type   CHAR(1)      NOT NULL,
  path        VARCHAR(200) DEFAULT '',
  component   VARCHAR(200) DEFAULT '',
  perms       VARCHAR(200) DEFAULT '',
  icon        VARCHAR(100) DEFAULT '',
  sort_order  INT          DEFAULT 0,
  visible     SMALLINT     DEFAULT 1,
  status      SMALLINT     DEFAULT 1,
  is_cache    SMALLINT     DEFAULT 0,
  create_by   VARCHAR(50)  DEFAULT '',
  create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  update_by   VARCHAR(50)  DEFAULT '',
  update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  remark      VARCHAR(500) DEFAULT ''
);

COMMENT ON TABLE  sys_menu IS '菜单表';
COMMENT ON COLUMN sys_menu.id IS '菜单ID';
COMMENT ON COLUMN sys_menu.parent_id IS '父菜单ID(0为顶级)';
COMMENT ON COLUMN sys_menu.menu_name IS '菜单名称';
COMMENT ON COLUMN sys_menu.menu_type IS '菜单类型(M目录 C菜单 F按钮)';
COMMENT ON COLUMN sys_menu.path IS '路由地址';
COMMENT ON COLUMN sys_menu.component IS '组件路径';
COMMENT ON COLUMN sys_menu.perms IS '权限标识(如 sys:user:list)';
COMMENT ON COLUMN sys_menu.icon IS '菜单图标';
COMMENT ON COLUMN sys_menu.sort_order IS '排序';
COMMENT ON COLUMN sys_menu.visible IS '是否可见(0隐藏 1显示)';
COMMENT ON COLUMN sys_menu.status IS '状态(0禁用 1启用)';
COMMENT ON COLUMN sys_menu.is_cache IS '是否缓存(0否 1是)';
COMMENT ON COLUMN sys_menu.create_by IS '创建者';
COMMENT ON COLUMN sys_menu.create_time IS '创建时间';
COMMENT ON COLUMN sys_menu.update_by IS '更新者';
COMMENT ON COLUMN sys_menu.update_time IS '更新时间';
COMMENT ON COLUMN sys_menu.remark IS '备注';

CREATE INDEX idx_menu_parent_id ON sys_menu (parent_id);

-- ----------------------------
-- 4. 部门表 (用于数据权限)
-- ----------------------------
DROP TABLE IF EXISTS sys_dept CASCADE;
CREATE TABLE sys_dept (
  id          BIGSERIAL    PRIMARY KEY,
  parent_id   BIGINT       DEFAULT 0,
  dept_name   VARCHAR(50)  NOT NULL,
  sort_order  INT          DEFAULT 0,
  leader      VARCHAR(50)  DEFAULT '',
  phone       VARCHAR(20)  DEFAULT '',
  email       VARCHAR(100) DEFAULT '',
  status      SMALLINT     DEFAULT 1,
  del_flag    SMALLINT     DEFAULT 0,
  create_by   VARCHAR(50)  DEFAULT '',
  create_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP,
  update_by   VARCHAR(50)  DEFAULT '',
  update_time TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON TABLE  sys_dept IS '部门表';
COMMENT ON COLUMN sys_dept.id IS '部门ID';
COMMENT ON COLUMN sys_dept.parent_id IS '父部门ID(0为顶级)';
COMMENT ON COLUMN sys_dept.dept_name IS '部门名称';
COMMENT ON COLUMN sys_dept.sort_order IS '排序';
COMMENT ON COLUMN sys_dept.leader IS '负责人';
COMMENT ON COLUMN sys_dept.phone IS '联系电话';
COMMENT ON COLUMN sys_dept.email IS '邮箱';
COMMENT ON COLUMN sys_dept.status IS '状态(0禁用 1启用)';
COMMENT ON COLUMN sys_dept.del_flag IS '删除标志(0正常 1已删除)';
COMMENT ON COLUMN sys_dept.create_by IS '创建者';
COMMENT ON COLUMN sys_dept.create_time IS '创建时间';
COMMENT ON COLUMN sys_dept.update_by IS '更新者';
COMMENT ON COLUMN sys_dept.update_time IS '更新时间';

CREATE INDEX idx_dept_parent_id ON sys_dept (parent_id);

-- ----------------------------
-- 5. 用户-角色关联表
-- ----------------------------
DROP TABLE IF EXISTS sys_user_role CASCADE;
CREATE TABLE sys_user_role (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id)
);

COMMENT ON TABLE sys_user_role IS '用户角色关联表';

-- ----------------------------
-- 6. 角色-菜单关联表 (菜单权限 + 按钮权限)
-- ----------------------------
DROP TABLE IF EXISTS sys_role_menu CASCADE;
CREATE TABLE sys_role_menu (
  role_id BIGINT NOT NULL,
  menu_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, menu_id)
);

COMMENT ON TABLE sys_role_menu IS '角色菜单关联表';

-- ----------------------------
-- 7. 角色-部门关联表 (数据配置权限)
-- ----------------------------
DROP TABLE IF EXISTS sys_role_dept CASCADE;
CREATE TABLE sys_role_dept (
  role_id BIGINT NOT NULL,
  dept_id BIGINT NOT NULL,
  PRIMARY KEY (role_id, dept_id)
);

COMMENT ON TABLE sys_role_dept IS '角色部门关联表(数据权限)';

-- ----------------------------
-- 自动更新 update_time 触发器函数
-- ----------------------------
CREATE OR REPLACE FUNCTION update_modified_timestamp()
RETURNS TRIGGER AS $$
BEGIN
    NEW.update_time = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- 为各业务表创建触发器
DO $$
DECLARE
    tbl TEXT;
BEGIN
    FOREACH tbl IN ARRAY ARRAY[
        'sys_user', 'sys_role', 'sys_menu', 'sys_dept'
    ] LOOP
        EXECUTE format(
            'CREATE TRIGGER trg_%I_update_time
             BEFORE UPDATE ON %I
             FOR EACH ROW
             EXECUTE FUNCTION update_modified_timestamp()',
            tbl, tbl
        );
    END LOOP;
END;
$$;

-- =============================================
-- 初始化数据
-- =============================================

-- 初始超级管理员用户 (密码: admin123, BCrypt加密)
INSERT INTO sys_user (id, username, password, nickname, status, data_scope, create_by) VALUES
(1, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', 1, 1, 'admin');

-- 初始角色
INSERT INTO sys_role (id, role_name, role_code, sort_order, data_scope, create_by) VALUES
(1, '超级管理员', 'admin', 1, 1, 'admin'),
(2, '普通角色', 'common', 2, 5, 'admin');

-- 用户-角色关联
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1);

-- 初始菜单数据
INSERT INTO sys_menu (id, parent_id, menu_name, menu_type, path, component, perms, icon, sort_order) VALUES
-- 一级目录
(1, 0, '系统管理', 'M', '/system', '', '', 'Setting', 1),
(2, 0, '系统监控', 'M', '/monitor', '', '', 'Monitor', 2),

-- 系统管理 - 子菜单
(100, 1, '用户管理', 'C', '/system/user', 'system/user/index', 'system:user:list', 'User', 1),
(101, 1, '角色管理', 'C', '/system/role', 'system/role/index', 'system:role:list', 'UserFilled', 2),
(102, 1, '菜单管理', 'C', '/system/menu', 'system/menu/index', 'system:menu:list', 'Menu', 3),
(103, 1, '部门管理', 'C', '/system/dept', 'system/dept/index', 'system:dept:list', 'OfficeBuilding', 4),

-- 用户管理 - 按钮权限
(1001, 100, '用户查询', 'F', '', '', 'system:user:query', '', 1),
(1002, 100, '用户新增', 'F', '', '', 'system:user:add', '', 2),
(1003, 100, '用户修改', 'F', '', '', 'system:user:edit', '', 3),
(1004, 100, '用户删除', 'F', '', '', 'system:user:remove', '', 4),
(1005, 100, '重置密码', 'F', '', '', 'system:user:resetPwd', '', 5),
(1006, 100, '导出用户', 'F', '', '', 'system:user:export', '', 6),

-- 角色管理 - 按钮权限
(1011, 101, '角色查询', 'F', '', '', 'system:role:query', '', 1),
(1012, 101, '角色新增', 'F', '', '', 'system:role:add', '', 2),
(1013, 101, '角色修改', 'F', '', '', 'system:role:edit', '', 3),
(1014, 101, '角色删除', 'F', '', '', 'system:role:remove', '', 4),
(1015, 101, '分配权限', 'F', '', '', 'system:role:assign', '', 5),

-- 菜单管理 - 按钮权限
(1021, 102, '菜单查询', 'F', '', '', 'system:menu:query', '', 1),
(1022, 102, '菜单新增', 'F', '', '', 'system:menu:add', '', 2),
(1023, 102, '菜单修改', 'F', '', '', 'system:menu:edit', '', 3),
(1024, 102, '菜单删除', 'F', '', '', 'system:menu:remove', '', 4),

-- 部门管理 - 按钮权限
(1031, 103, '部门查询', 'F', '', '', 'system:dept:query', '', 1),
(1032, 103, '部门新增', 'F', '', '', 'system:dept:add', '', 2),
(1033, 103, '部门修改', 'F', '', '', 'system:dept:edit', '', 3),
(1034, 103, '部门删除', 'F', '', '', 'system:dept:remove', '', 4);

-- 超级管理员拥有所有菜单权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, id FROM sys_menu;

-- 初始部门
INSERT INTO sys_dept (id, parent_id, dept_name, sort_order, leader) VALUES
(1, 0, '总公司', 1, 'admin'),
(2, 1, '研发部', 1, ''),
(3, 1, '市场部', 2, ''),
(4, 1, '测试部', 3, ''),
(5, 2, '前端组', 1, ''),
(6, 2, '后端组', 2, '');

-- 重置序列（确保后续插入ID自增正确）
SELECT setval('sys_user_id_seq', (SELECT COALESCE(MAX(id), 1) FROM sys_user));
SELECT setval('sys_role_id_seq', (SELECT COALESCE(MAX(id), 1) FROM sys_role));
SELECT setval('sys_menu_id_seq', (SELECT COALESCE(MAX(id), 1) FROM sys_menu));
SELECT setval('sys_dept_id_seq', (SELECT COALESCE(MAX(id), 1) FROM sys_dept));
