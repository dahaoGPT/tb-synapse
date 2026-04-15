package com.admin.mapper;

import com.admin.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据用户ID查询角色列表
     */
    List<com.admin.entity.SysRole> selectRolesByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询权限标识列表
     */
    List<String> selectPermsByUserId(@Param("userId") Long userId);
}
