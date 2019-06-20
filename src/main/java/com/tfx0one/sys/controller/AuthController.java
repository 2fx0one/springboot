package com.tfx0one.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tfx0one.common.api.R;
import com.tfx0one.common.exception.CommonException;
import com.tfx0one.common.utils.JWTUtils;
import com.tfx0one.common.utils.ShiroUtils;
import com.tfx0one.sys.entity.SysUser;
import com.tfx0one.sys.service.SysUserService;
import com.tfx0one.sys.vo.request.ApiLoginUser;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @projectName: base-web
 * @author: wangk
 * @date: 2019/1/24 17:39
 * @Version: 1.0
 */

@RestController
@RequestMapping("/api/sys")
public class AuthController {

    @Autowired
    private SysUserService userService;

    @PostMapping("/login")
    public R login(@RequestBody ApiLoginUser login) {
        SysUser user = userService.getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getLoginName, login.getUsername()));
        if (user == null) {
            throw new CommonException("用户不存在！");
        }
        String salt = user.getId();
        String simpleHashPassword = ShiroUtils.md5(login.getPassword(), salt);
        if (!user.getPassword().equals(simpleHashPassword)) {
            throw new CommonException("密码不正确！");
        }
        return R.ok(JWTUtils.sign(user));
    }

    @PostMapping("/logout")
    @RequiresAuthentication
    public R logout() {
        //jwtToken 并未失效 要等过期之后了。故而前端逻辑需要配合把jwtToken删除
        ShiroUtils.getSubject().logout();
        return R.ok("logout success");
    }

}
