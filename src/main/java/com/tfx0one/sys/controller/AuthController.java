package com.tfx0one.sys.controller;

import com.tfx0one.common.api.R;
import com.tfx0one.common.base.BaseController;
import com.tfx0one.common.utils.JWTUtils;
import com.tfx0one.common.utils.ShiroUtils;
import com.tfx0one.sys.entity.Menu;
import com.tfx0one.sys.entity.Role;
import com.tfx0one.sys.entity.User;
import com.tfx0one.sys.service.RoleService;
import com.tfx0one.sys.service.UserService;
import com.tfx0one.sys.vo.ApiRequestLoginUser;
import com.tfx0one.sys.vo.ApiResponseUserInfo;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @projectName: base-web
 * @author: wangk
 * @date: 2019/1/24 17:39
 * @Version: 1.0
 */

@RestController
@RequestMapping("/sys/auth")
public class AuthController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;


    @PostMapping("/login")
    public R login(@RequestBody ApiRequestLoginUser login) {
        User user = userService.getByLoginName(login.getUsername());
        if (user == null) {
            return R.error(401, "用户不存在");
        }
        String salt = user.getId();
        String simpleHashPassword = ShiroUtils.md5(login.getPassword(), salt);
        if (user.getPassword().equals(simpleHashPassword)) {
            return R.ok("login success!", JWTUtils.sign(user));
        } else {
            throw new AuthenticationException("账号或密码错误！");
        }
    }

    @PostMapping("/logout")
    @RequiresAuthentication
    public R logout() {
        ShiroUtils.getSubject().logout();
        return R.ok("logout success");
    }

    @GetMapping("/user/info")
    @RequiresAuthentication
    public R<ApiResponseUserInfo> userInfo() {
        //用户角色信息 菜单 权限
        List<Menu> menuList = ShiroUtils.getCurrentUser().getMenuList();
//        List<Role> roleList = ShiroUtils.getCurrentUser().getRoleList();
        AuthorizationInfo info = ShiroUtils.getAuthorizationInfo();

        return R.ok("success",
                ApiResponseUserInfo.create(
                        menuList,
                        info.getStringPermissions()));
    }


}
