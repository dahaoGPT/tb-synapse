package com.admin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色实体
 */
@Data
@TableName("sys_role")
public class SysRole implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 角色名称 */
    private String roleName;

    /** 角色编码 */
    private String roleCode;

    /** 排序 */
    private Integer sortOrder;

    /** 状态(0禁用 1启用) */
    private Integer status;

    /** 数据范围(1全部 2自定义 3本部门 4本部门及以下 5仅本人) */
    private Integer dataScope;

    /** 删除标志(0正常 1已删除) */
    @TableLogic
    private Integer delFlag;

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

    /** 菜单ID列表 */
    @TableField(exist = false)
    private java.util.List<Long> menuIds;

    /** 部门ID列表 */
    @TableField(exist = false)
    private java.util.List<Long> deptIds;
}
