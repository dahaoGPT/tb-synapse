package com.admin.service.impl;

import com.admin.common.BusinessException;
import com.admin.entity.SysRole;
import com.admin.entity.SysRoleDept;
import com.admin.entity.SysRoleMenu;
import com.admin.mapper.SysRoleDeptMapper;
import com.admin.mapper.SysRoleMapper;
import com.admin.mapper.SysRoleMenuMapper;
import com.admin.service.SysRoleService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现
 */
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysRoleMenuMapper sysRoleMenuMapper;

    @Resource
    private SysRoleDeptMapper sysRoleDeptMapper;

    @Override
    public IPage<SysRole> selectRolePage(int pageNum, int pageSize, String roleName, String roleCode, Integer status) {
        Page<SysRole> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotBlank(roleName), SysRole::getRoleName, roleName);
        wrapper.like(StringUtils.isNotBlank(roleCode), SysRole::getRoleCode, roleCode);
        wrapper.eq(status != null, SysRole::getStatus, status);
        wrapper.orderByAsc(SysRole::getSortOrder);
        return sysRoleMapper.selectPage(page, wrapper);
    }

    @Override
    public List<SysRole> selectRolesByUserId(Long userId) {
        return sysRoleMapper.selectRolesByUserId(userId);
    }

    @Override
    public List<Long> selectMenuIdsByRoleId(Long roleId) {
        return sysRoleMapper.selectMenuIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignMenus(Long roleId, List<Long> menuIds) {
        // 先删除原有关系
        LambdaQueryWrapper<SysRoleMenu> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysRoleMenu::getRoleId, roleId);
        sysRoleMenuMapper.delete(deleteWrapper);
        // 再批量插入新关系
        if (menuIds != null && !menuIds.isEmpty()) {
            for (Long menuId : menuIds) {
                SysRoleMenu roleMenu = new SysRoleMenu();
                roleMenu.setRoleId(roleId);
                roleMenu.setMenuId(menuId);
                sysRoleMenuMapper.insert(roleMenu);
            }
        }
    }

    @Override
    public List<Long> selectDeptIdsByRoleId(Long roleId) {
        return sysRoleMapper.selectDeptIdsByRoleId(roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignDepts(Long roleId, List<Long> deptIds) {
        // 先删除原有关系
        LambdaQueryWrapper<SysRoleDept> deleteWrapper = new LambdaQueryWrapper<>();
        deleteWrapper.eq(SysRoleDept::getRoleId, roleId);
        sysRoleDeptMapper.delete(deleteWrapper);
        // 再批量插入新关系
        if (deptIds != null && !deptIds.isEmpty()) {
            for (Long deptId : deptIds) {
                SysRoleDept roleDept = new SysRoleDept();
                roleDept.setRoleId(roleId);
                roleDept.setDeptId(deptId);
                sysRoleDeptMapper.insert(roleDept);
            }
        }
    }
}
