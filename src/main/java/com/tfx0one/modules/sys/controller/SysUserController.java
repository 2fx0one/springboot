package com.tfx0one.modules.sys.controller;

import com.tfx0one.common.annotation.SysLog;
import com.tfx0one.common.constant.GlobalConstant;
import com.tfx0one.common.utils.Pagination;
import com.tfx0one.common.utils.R;
import com.tfx0one.common.validator.Assert;
import com.tfx0one.common.validator.ValidatorUtils;
import com.tfx0one.common.validator.group.AddGroup;
import com.tfx0one.common.validator.group.UpdateGroup;
import com.tfx0one.modules.sys.entity.SysUserEntity;
import com.tfx0one.modules.sys.service.SysUserRoleService;
import com.tfx0one.modules.sys.service.SysUserService;
import com.tfx0one.modules.sys.vo.RequestPassword;
import com.tfx0one.modules.sys.vo.ResponseUserInfo;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.session.RedisSessionProperties;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/sys/user")
public class SysUserController extends AbstractBaseController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserRoleService sysUserRoleService;


    /**
     * 所有用户列表
     */
    @GetMapping("/list")
    @RequiresPermissions("sys:user:list")
    public R<Pagination<SysUserEntity>> list(@RequestParam Map<String, Object> params, SysUserEntity sysUser) {
        //只有超级管理员，才能查看所有管理员列表
        if (getUserId() != GlobalConstant.SUPER_ADMIN) {
            params.put("createUserId", getUserId());
            sysUser.setCreateUserId(getUserId());
        }
        return R.ok(sysUserService.queryPage(params, sysUser));
    }

    /**
     * 获取当前登录的用户信息
     */
    @GetMapping("/info")
    public R<ResponseUserInfo> info() {
        SysUserEntity user = getUser();
        Set<String> permissions = sysUserService.queryAllPerms(getUserId());
        return R.ok(ResponseUserInfo.create(user, permissions));
    }

    /**
     * 修改登录用户密码
     */
    @SysLog("修改密码")
    @PostMapping("/password")
    public R password(@Validated @RequestBody RequestPassword form) {
//        ValidatorUtils.validateEntity(form);
        Assert.notBlank(form.getNewPassword(), "新密码不为能空");

        //sha256加密
        String password = new Sha256Hash(form.getPassword(), getUser().getSalt()).toHex();
        //sha256加密
        String newPassword = new Sha256Hash(form.getNewPassword(), getUser().getSalt()).toHex();

        //更新密码
        boolean flag = sysUserService.updatePassword(getUserId(), password, newPassword);
        if (!flag) {
            return R.error("原密码不正确");
        }

        return R.ok();
    }

    /**
     * 用户信息
     */
    @GetMapping("/info/{userId}")
    @RequiresPermissions("sys:user:info")
    public R<SysUserEntity> info(@PathVariable("userId") Long userId) {
        SysUserEntity user = sysUserService.getById(userId);

        //获取用户所属的角色列表
        List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
        user.setRoleIdList(roleIdList);

        return R.ok(user);
    }

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 保存用户
     */
    @SysLog("保存用户")
    @PostMapping("/save")
    @RequiresPermissions("sys:user:save")
    public R save(@Validated({AddGroup.class}) @RequestBody SysUserEntity user) {
//        ValidatorUtils.validateEntity(user, AddGroup.class);

        user.setCreateUserId(getUserId());
        sysUserService.saveUser(user);

        return R.ok();
    }

    /**
     * 修改用户
     */
    @SysLog("修改用户")
    @PostMapping("/update")
    @RequiresPermissions("sys:user:update")
    public R update(@Validated({UpdateGroup.class}) @RequestBody SysUserEntity user) {
//        ValidatorUtils.validateEntity(user, UpdateGroup.class);

        user.setCreateUserId(getUserId());
        sysUserService.update(user);

        return R.ok("修改用户成功");
    }

    /**
     * 删除用户
     */
    @SysLog("删除用户")
    @PostMapping("/delete")
    @RequiresPermissions("sys:user:delete")
    public R delete(@RequestBody Long[] userIds) {
        if (ArrayUtils.contains(userIds, 1L)) {
            return R.error("系统管理员不能删除");
        }

        if (ArrayUtils.contains(userIds, getUserId())) {
            return R.error("当前用户不能删除");
        }

        sysUserService.deleteBatch(userIds);

        return R.ok();
    }
}
