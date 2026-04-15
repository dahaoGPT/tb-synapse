package com.admin.service;

import com.admin.entity.SysDept;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 部门Service接口
 */
public interface SysDeptService extends IService<SysDept> {

    /**
     * 获取所有部门列表
     */
    List<SysDept> selectDeptList();

    /**
     * 构建部门树
     */
    List<SysDept> buildDeptTree(List<SysDept> depts);

    /**
     * 获取部门下拉树
     */
    List<SysDept> selectDeptTreeselect();
}
