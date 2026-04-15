package com.admin.service.impl;

import com.admin.entity.SysMenu;
import com.admin.mapper.SysMenuMapper;
import com.admin.service.SysMenuService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 菜单Service实现
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Resource
    private SysMenuMapper sysMenuMapper;

    @Override
    public List<SysMenu> selectMenuList() {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(SysMenu::getParentId).orderByAsc(SysMenu::getSortOrder);
        return sysMenuMapper.selectList(wrapper);
    }

    @Override
    public List<SysMenu> selectMenusByUserId(Long userId) {
        return sysMenuMapper.selectMenusByUserId(userId);
    }

    @Override
    public List<SysMenu> selectRouteTree(Long userId) {
        List<SysMenu> menus = sysMenuMapper.selectMenusByUserId(userId);
        // 过滤出菜单类型为M和C的
        List<SysMenu> routeMenus = menus.stream()
                .filter(m -> "M".equals(m.getMenuType()) || "C".equals(m.getMenuType()))
                .collect(Collectors.toList());
        return buildMenuTree(routeMenus);
    }

    @Override
    public List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        List<SysMenu> trees = new ArrayList<>();
        List<Long> ids = menus.stream().map(SysMenu::getId).collect(Collectors.toList());
        for (SysMenu menu : menus) {
            // 如果父ID不在当前列表中，则为顶级节点
            if (!ids.contains(menu.getParentId())) {
                buildChildren(menu, menus);
                trees.add(menu);
            }
        }
        // 如果没有找到顶级节点（可能parent_id=0不在列表中），以parent_id=0为根
        if (trees.isEmpty()) {
            for (SysMenu menu : menus) {
                if (menu.getParentId() == null || menu.getParentId() == 0) {
                    buildChildren(menu, menus);
                    trees.add(menu);
                }
            }
        }
        return trees;
    }

    /**
     * 递归构建子菜单
     */
    private void buildChildren(SysMenu parent, List<SysMenu> allMenus) {
        List<SysMenu> children = allMenus.stream()
                .filter(m -> parent.getId().equals(m.getParentId()))
                .collect(Collectors.toList());
        for (SysMenu child : children) {
            buildChildren(child, allMenus);
        }
        parent.setChildren(children);
    }

    @Override
    public List<SysMenu> selectMenuTreeselect() {
        List<SysMenu> menus = selectMenuList();
        return buildMenuTree(menus);
    }
}
