package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 菜单实体
 */
@Data
@TableName("sys_menu")
public class SysMenu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 父菜单ID(0为顶级) */
    private Long parentId;

    /** 菜单名称 */
    private String menuName;

    /** 菜单类型(M目录 C菜单 F按钮) */
    private String menuType;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识(如 sys:user:list) */
    private String perms;

    /** 菜单图标 */
    private String icon;

    /** 排序 */
    private Integer sortOrder;

    /** 是否可见(0隐藏 1显示) */
    private Integer visible;

    /** 状态(0禁用 1启用) */
    private Integer status;

    /** 是否缓存(0否 1是) */
    private Integer isCache;

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

    /** 子菜单列表 */
    @TableField(exist = false)
    private List<SysMenu> children;
}
