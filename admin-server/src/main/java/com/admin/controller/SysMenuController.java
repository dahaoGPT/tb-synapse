package com.admin.controller;

import com.admin.common.BusinessException;
import com.admin.common.Result;
import com.admin.entity.SysMenu;
import com.admin.service.SysMenuService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 菜单管理控制器
 */
@RestController
@RequestMapping("/api/system/menu")
public class SysMenuController {

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 获取菜单列表（全部，用于树形展示）
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/list")
    public Result<List<SysMenu>> list() {
        List<SysMenu> menus = sysMenuService.selectMenuList();
        List<SysMenu> tree = sysMenuService.buildMenuTree(menus);
        return Result.success(tree);
    }

    /**
     * 获取菜单详情
     */
    @PreAuthorize("hasAuthority('system:menu:query')")
    @GetMapping("/{id}")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        SysMenu menu = sysMenuService.getById(id);
        if (menu == null) {
            throw new BusinessException("菜单不存在");
        }
        return Result.success(menu);
    }

    /**
     * 新增菜单
     */
    @PreAuthorize("hasAuthority('system:menu:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysMenu menu) {
        sysMenuService.save(menu);
        return Result.success();
    }

    /**
     * 修改菜单
     */
    @PreAuthorize("hasAuthority('system:menu:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysMenu menu) {
        sysMenuService.updateById(menu);
        return Result.success();
    }

    /**
     * 删除菜单
     */
    @PreAuthorize("hasAuthority('system:menu:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        // 检查是否有子菜单
        long childCount = sysMenuService.count(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SysMenu>()
                        .eq(SysMenu::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
        sysMenuService.removeById(id);
        return Result.success();
    }

    /**
     * 获取菜单下拉树
     */
    @PreAuthorize("hasAuthority('system:menu:list')")
    @GetMapping("/treeselect")
    public Result<List<SysMenu>> treeselect() {
        List<SysMenu> tree = sysMenuService.selectMenuTreeselect();
        return Result.success(tree);
    }
}
