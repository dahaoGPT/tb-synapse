package com.admin.service;

import com.admin.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 用户Service接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser selectUserByUsername(String username);

    /**
     * 分页查询用户列表
     */
    com.baomidou.mybatisplus.core.metadata.IPage<SysUser> selectUserPage(int pageNum, int pageSize, String username, String phone, Integer status);

    /**
     * 查询用户信息（含角色和权限）
     */
    SysUser getUserInfo(Long userId);

    /**
     * 重置用户密码
     */
    void resetUserPassword(Long userId, String password);

    /**
     * 修改用户状态
     */
    void changeUserStatus(Long userId, Integer status);
}
