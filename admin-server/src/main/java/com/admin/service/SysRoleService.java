package com.admin.service;

import com.admin.entity.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 角色Service接口
 */
public interface SysRoleService extends IService<SysRole> {

    /**
     * 分页查询角色列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<SysRole> selectRolePage(int pageNum, int pageSize, String roleName, String roleCode, Integer status);

    /**
     * 根据用户ID查询角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 获取角色已分配的菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);

    /**
     * 分配菜单权限
     */
    void assignMenus(Long roleId, List<Long> menuIds);

    /**
     * 获取角色已分配的部门ID列表
     */
    List<Long> selectDeptIdsByRoleId(Long roleId);

    /**
     * 分配数据权限
     */
    void assignDepts(Long roleId, List<Long> deptIds);
}
