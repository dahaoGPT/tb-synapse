package com.admin.service.impl;

import com.admin.entity.SysDept;
import com.admin.mapper.SysDeptMapper;
import com.admin.service.SysDeptService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 部门Service实现
 */
@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    @Override
    public List<SysDept> selectDeptList() {
        LambdaQueryWrapper<SysDept> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysDept::getParentId).orderByAsc(SysDept::getSortOrder);
        return sysDeptMapper.selectList(wrapper);
    }

    @Override
    public List<SysDept> buildDeptTree(List<SysDept> depts) {
        List<SysDept> trees = new ArrayList<>();
        List<Long> ids = depts.stream().map(SysDept::getId).collect(Collectors.toList());
        for (SysDept dept : depts) {
            if (!ids.contains(dept.getParentId())) {
                buildChildren(dept, depts);
                trees.add(dept);
            }
        }
        // 如果没有找到顶级节点，以parent_id=0为根
        if (trees.isEmpty()) {
            for (SysDept dept : depts) {
                if (dept.getParentId() == null || dept.getParentId() == 0) {
                    buildChildren(dept, depts);
                    trees.add(dept);
                }
            }
        }
        return trees;
    }

    /**
     * 递归构建子部门
     */
    private void buildChildren(SysDept parent, List<SysDept> allDepts) {
        List<SysDept> children = allDepts.stream()
                .filter(d -> parent.getId().equals(d.getParentId()))
                .collect(Collectors.toList());
        for (SysDept child : children) {
            buildChildren(child, allDepts);
        }
        parent.setChildren(children);
    }

    @Override
    public List<SysDept> selectDeptTreeselect() {
        List<SysDept> depts = selectDeptList();
        return buildDeptTree(depts);
    }
}
