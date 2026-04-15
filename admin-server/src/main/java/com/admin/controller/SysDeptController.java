package com.admin.controller;

import com.admin.common.BusinessException;
import com.admin.common.Result;
import com.admin.entity.SysDept;
import com.admin.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 部门管理控制器
 */
@RestController
@RequestMapping("/api/system/dept")
public class SysDeptController {

    @Resource
    private SysDeptService sysDeptService;

    /**
     * 获取部门列表（全部，用于树形展示）
     */
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/list")
    public Result<List<SysDept>> list() {
        List<SysDept> depts = sysDeptService.selectDeptList();
        List<SysDept> tree = sysDeptService.buildDeptTree(depts);
        return Result.success(tree);
    }

    /**
     * 获取部门详情
     */
    @PreAuthorize("hasAuthority('system:dept:query')")
    @GetMapping("/{id}")
    public Result<SysDept> getInfo(@PathVariable Long id) {
        SysDept dept = sysDeptService.getById(id);
        if (dept == null) {
            throw new BusinessException("部门不存在");
        }
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @PreAuthorize("hasAuthority('system:dept:add')")
    @PostMapping
    public Result<Void> add(@RequestBody SysDept dept) {
        sysDeptService.save(dept);
        return Result.success();
    }

    /**
     * 修改部门
     */
    @PreAuthorize("hasAuthority('system:dept:edit')")
    @PutMapping
    public Result<Void> edit(@RequestBody SysDept dept) {
        sysDeptService.updateById(dept);
        return Result.success();
    }

    /**
     * 删除部门
     */
    @PreAuthorize("hasAuthority('system:dept:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        // 检查是否有子部门
        long childCount = sysDeptService.count(
                new LambdaQueryWrapper<SysDept>().eq(SysDept::getParentId, id));
        if (childCount > 0) {
            throw new BusinessException("存在子部门，不允许删除");
        }
        sysDeptService.removeById(id);
        return Result.success();
    }

    /**
     * 获取部门下拉树
     */
    @PreAuthorize("hasAuthority('system:dept:list')")
    @GetMapping("/treeselect")
    public Result<List<SysDept>> treeselect() {
        List<SysDept> tree = sysDeptService.selectDeptTreeselect();
        return Result.success(tree);
    }
}
