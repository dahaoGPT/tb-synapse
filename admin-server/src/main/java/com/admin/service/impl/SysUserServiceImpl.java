package com.admin.service.impl;

import com.admin.common.BusinessException;
import com.admin.entity.SysRole;
import com.admin.entity.SysUser;
import com.admin.mapper.SysUserMapper;
import com.admin.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户Service实现
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Override
    public SysUser selectUserByUsername(String username) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUser::getUsername, username);
        return sysUserMapper.selectOne(wrapper);
    }

    @Override
    public IPage<SysUser> selectUserPage(int pageNum, int pageSize, String username, String phone, Integer status) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(username), SysUser::getUsername, username);
        wrapper.like(StringUtils.isNotBlank(phone), SysUser::getPhone, phone);
        wrapper.eq(status != null, SysUser::getStatus, status);
        wrapper.orderByAsc(SysUser::getId);
        return sysUserMapper.selectPage(page, wrapper);
    }

    @Override
    public SysUser getUserInfo(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        // 查询角色列表
        List<SysRole> roles = sysUserMapper.selectRolesByUserId(userId);
        user.setRoles(roles);
        // 查询权限标识列表
        List<String> perms = sysUserMapper.selectPermsByUserId(userId);
        user.setPermissions(new HashSet<>(perms));
        return user;
    }

    @Override
    public void resetUserPassword(Long userId, String password) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setPassword(passwordEncoder.encode(password));
        sysUserMapper.updateById(user);
    }

    @Override
    public void changeUserStatus(Long userId, Integer status) {
        SysUser user = new SysUser();
        user.setId(userId);
        user.setStatus(status);
        sysUserMapper.updateById(user);
    }
}
