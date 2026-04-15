package com.admin.controller;

import com.admin.common.BusinessException;
import com.admin.common.PageResult;
import com.admin.common.Result;
import com.admin.entity.SysRole;
import com.admin.service.SysRoleService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 */
@RestController
@RequestMapping("/api/system/role")
public class SysRoleController {

    @Resource
    private SysRoleService sysRoleService;

    /**
     * 分页查询角色列表
     */
    @PreAuthorize("hasAuthority('system:role:list')")
    @GetMapping("/list")
    public Result<PageResult<SysRole>> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String roleName,
            @RequestParam(required = false) String roleCode,
            @RequestParam(required = false) Integer status) {
        IPage<SysRole> page = sysRoleService.selectRolePage(pageNum, pageSize, roleName, roleCode, status);
        return Result.success(PageResult.of(page));
    }

    /**
     * 获取角色详情
     */
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/{id}")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        SysRole role = sysRoleService.getById(id);
        if (role == null) {
            throw new BusinessException("角色不存在");
        }
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PreAuthorize("hasAuthority('system:role:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysRole role) {
        sysRoleService.save(role);
        return Result.success();
    }

    /**
     * 修改角色
     */
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysRole role) {
        sysRoleService.updateById(role);
        return Result.success();
    }

    /**
     * 批量删除角色
     */
    @PreAuthorize("hasAuthority('system:role:remove')")
    @DeleteMapping("/{ids}")
    public Result<Void> remove(@PathVariable String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        sysRoleService.removeByIds(idList);
        return Result.success();
    }

    /**
     * 修改角色状态
     */
    @PreAuthorize("hasAuthority('system:role:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysRole role) {
        SysRole updateRole = new SysRole();
        updateRole.setId(role.getId());
        updateRole.setStatus(role.getStatus());
        sysRoleService.updateById(updateRole);
        return Result.success();
    }

    /**
     * 获取角色已分配的菜单ID列表
     */
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> getMenuIds(@PathVariable Long roleId) {
        List<Long> menuIds = sysRoleService.selectMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }

    /**
     * 分配菜单权限
     */
    @PreAuthorize("hasAuthority('system:role:assign')")
    @PutMapping("/assignMenus")
    public Result<Void> assignMenus(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> menuIds = ((List<Number>) params.get("menuIds")).stream()
                .map(Number::longValue)
                .collect(Collectors.toList());
        sysRoleService.assignMenus(roleId, menuIds);
        return Result.success();
    }

    /**
     * 获取角色已分配的部门ID列表
     */
    @PreAuthorize("hasAuthority('system:role:query')")
    @GetMapping("/deptIds/{roleId}")
    public Result<List<Long>> getDeptIds(@PathVariable Long roleId) {
        List<Long> deptIds = sysRoleService.selectDeptIdsByRoleId(roleId);
        return Result.success(deptIds);
    }

    /**
     * 分配数据权限
     */
    @PreAuthorize("hasAuthority('system:role:assign')")
    @PutMapping("/assignDepts")
    public Result<Void> assignDepts(@RequestBody Map<String, Object> params) {
        Long roleId = Long.valueOf(params.get("roleId").toString());
        @SuppressWarnings("unchecked")
        List<Long> deptIds = ((List<Number>) params.get("deptIds")).stream()
                .map(Number::longValue)
                .collect(Collectors.toList());
        sysRoleService.assignDepts(roleId, deptIds);
        return Result.success();
    }
}
