package com.admin.controller;

import com.admin.common.BusinessException;
import com.admin.common.PageResult;
import com.admin.common.Result;
import com.admin.entity.SysUser;
import com.admin.entity.SysUserRole;
import com.admin.entity.SysDept;
import com.admin.mapper.SysUserRoleMapper;
import com.admin.mapper.SysDeptMapper;
import com.admin.service.SysUserService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/api/system/user")
public class SysUserController {

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysUserRoleMapper sysUserRoleMapper;

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Resource
    private PasswordEncoder passwordEncoder;

    /**
     * 分页查询用户列表
     */
    @PreAuthorize("hasAuthority('system:user:list')")
    @GetMapping("/list")
    public Result<PageResult<SysUser>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) Integer status) {
        IPage<SysUser> page = sysUserService.selectUserPage(pageNum, pageSize, username, phone, status);
        // 清除密码并填充部门名称
        List<SysUser> records = page.getRecords();
        // 收集所有非空的deptId
        Set<Long> deptIds = records.stream()
                .map(SysUser::getDeptId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        // 批量查询部门
        Map<Long, String> deptNameMap = new HashMap<>();
        if (!deptIds.isEmpty()) {
            List<SysDept> depts = sysDeptMapper.selectBatchIds(deptIds);
            depts.forEach(d -> deptNameMap.put(d.getId(), d.getDeptName()));
        }
        records.forEach(u -> {
            u.setPassword(null);
            if (u.getDeptId() != null) {
                u.setDeptName(deptNameMap.getOrDefault(u.getDeptId(), ""));
            }
        });
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取用户详情
     */
    @PreAuthorize("hasAuthority('system:user:query')")
    @GetMapping("/{id}")
    public Result<SysUser> getInfo(@PathVariable Long id) {
        SysUser user = sysUserService.getById(id);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setPassword(null);
        // 查询角色ID列表
        List<SysUserRole> userRoles = sysUserRoleMapper.selectList(
                new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, id));
        Long[] roleIds = userRoles.stream().map(SysUserRole::getRoleId).toArray(Long[]::new);
        user.setRoleIds(roleIds);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @PreAuthorize("hasAuthority('system:user:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysUser user) {
        // 检查用户名是否已存在
        SysUser existUser = sysUserService.selectUserByUsername(user.getUsername());
        if (existUser != null) {
            throw new BusinessException("用户名已存在");
        }
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.save(user);
        return Result.success();
    }

    /**
     * 修改用户
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysUser user) {
        // 如果密码不为空，则加密
        if (StringUtils.isNotBlank(user.getPassword())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        } else {
            user.setPassword(null);
        }
        user.setUpdateTime(LocalDateTime.now());
        sysUserService.updateById(user);
        return Result.success();
    }

    /**
     * 批量删除用户
     */
    @PreAuthorize("hasAuthority('system:user:remove')")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        sysUserService.removeByIds(idList);
        return Result.success();
    }

    /**
     * 重置密码
     */
    @PreAuthorize("hasAuthority('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(@RequestBody SysUser user) {
        sysUserService.resetUserPassword(user.getId(), user.getPassword());
        return Result.success();
    }

    /**
     * 修改用户状态
     */
    @PreAuthorize("hasAuthority('system:user:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysUser user) {
        sysUserService.changeUserStatus(user.getId(), user.getStatus());
        return Result.success();
    }
}
