package com.admin.service;

import com.admin.entity.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 菜单Service接口
 */
public interface SysMenuService extends IService<SysMenu> {

    /**
     * 获取所有菜单列表
     */
    List<SysMenu> selectMenuList();

    /**
     * 根据用户ID查询菜单列表
     */
    List<SysMenu> selectMenusByUserId(Long userId);

    /**
     * 获取菜单路由树（仅M和C类型）
     */
    List<SysMenu> selectRouteTree(Long userId);

    /**
     * 构建菜单树
     */
    List<SysMenu> buildMenuTree(List<SysMenu> menus);

    /**
     * 获取菜单下拉树
     */
    List<SysMenu> selectMenuTreeselect();
}
