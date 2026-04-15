package com.admin.controller;

import com.admin.common.Result;
import com.admin.entity.SysMenu;
import com.admin.entity.SysUser;
import com.admin.security.JwtTokenProvider;
import com.admin.service.SysMenuService;
import com.admin.service.SysUserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Resource
    private AuthenticationManager authenticationManager;

    @Resource
    private JwtTokenProvider jwtTokenProvider;

    @Resource
    private SysUserService sysUserService;

    @Resource
    private SysMenuService sysMenuService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> loginForm) {
        String username = loginForm.get("username");
        String password = loginForm.get("password");
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = jwtTokenProvider.generateToken(username);
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        return Result.success(data);
    }

    /**
     * 获取当前用户信息（含角色、权限列表）
     */
    @GetMapping("/info")
    public Result<SysUser> getInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = sysUserService.selectUserByUsername(username);
        SysUser userInfo = sysUserService.getUserInfo(user.getId());
        // 清除密码，不返回给前端
        userInfo.setPassword(null);
        return Result.success(userInfo);
    }

    /**
     * 获取当前用户的菜单路由树（仅返回菜单类型为M和C的）
     */
    @GetMapping("/routes")
    public Result<List<SysMenu>> getRoutes() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        SysUser user = sysUserService.selectUserByUsername(username);
        List<SysMenu> routes = sysMenuService.selectRouteTree(user.getId());
        return Result.success(routes);
    }

    /**
     * 退出登录
     */
    @PostMapping("/logout")
    public Result<Void> logout() {
        SecurityContextHolder.clearContext();
        return Result.success();
    }
}
