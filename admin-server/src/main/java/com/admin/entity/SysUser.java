package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户实体
 */
@Data
@TableName("sys_user")
public class SysUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码(BCrypt加密) */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 邮箱 */
    private String email;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 部门ID */
    private Long deptId;

    /** 状态(0禁用 1启用) */
    private Integer status;

    /** 数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人) */
    private Integer dataScope;

    /** 删除标志(0正常 1已删除) */
    @TableLogic
    private Integer delFlag;

    /** 最后登录IP */
    private String loginIp;

    /** 最后登录时间 */
    private LocalDateTime loginTime;

    /** 创建者 */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新者 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 备注 */
    private String remark;

    // ========== 非数据库字段 ==========

    /** 部门名称 */
    @TableField(exist = false)
    private String deptName;

    /** 角色列表 */
    @TableField(exist = false)
    private java.util.List<SysRole> roles;

    /** 权限标识列表 */
    @TableField(exist = false)
    private java.util.Set<String> permissions;

    /** 角色ID列表 */
    @TableField(exist = false)
    private Long[] roleIds;
}
