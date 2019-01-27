/**
 * Copyright (c) 2015-2017, Chill Zhuang 庄骞 (smallchill@163.com).
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tfx0one.common.utils;

import com.tfx0one.common.constant.GlobalConstant;
import com.tfx0one.common.shiro.ShiroAuthRealm;
import com.tfx0one.common.shiro.ShiroConfig;
import com.tfx0one.sys.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.crypto.SecureRandomNumberGenerator;
import org.apache.shiro.crypto.hash.SimpleHash;
import org.apache.shiro.subject.Subject;

/**
 * shiro工具类
 *
 * @author dafei, Chill Zhuang
 */
public class ShiroUtils {

    public static void main(String[] args) {
        //所需加密的参数  即  密码
        String pwd = "123456";
        //[盐] 一般为用户名 或 随机数
        String salt = "1";
        //加密次数
        System.out.println(md5(pwd, salt));
        System.out.println(sha256(pwd, salt));
    }



    /**
     * 循环次数
     */
    public final static int HASH_ITERATIONS = 1;

    private static CacheManager cacheManager = SpringContextHolder.getBean(CacheManager.class);
    private static ShiroAuthRealm shiroAuthRealm = SpringContextHolder.getBean(ShiroAuthRealm.class);

    //清除全部缓存 直接在清空authz授权的缓存 和 身份认证authc的缓存
    public static void clearAllUserAuthCache() {
        cacheManager.getCache(ShiroConfig.AUTHENTICATION_CACHE_NAME).clear();
        cacheManager.getCache(ShiroConfig.AUTHORIZATION_CACHE_NAME).clear();
    }

    //清空某个用户的身份认证和授权信息。可以用在修改了密码的情况。
    public static void clearCurrentUserAuthorization() {
        shiroAuthRealm.doClearCache(getSubject().getPrincipals());
//        cacheManager.getCache(ShiroConfig.AUTHORIZATION_CACHE_NAME).remove(getSubject().getPrincipals());

    }

    //获取当前用户的授权信息，会放入缓存。登录时获取
    public static AuthorizationInfo getAuthorizationInfo() {
        return shiroAuthRealm.getAuthorizationInfo(getSubject().getPrincipals());
    }

    public static User getCurrentUser() {
        if (isUser()) {
            return (User) getSubject().getPrincipal();
        }else {
            throw new AuthenticationException("不是认证用户！");
        }
    }


    /**
     * shiro密码加密工具类 散列算法
     *
     * @param credentials 密码
     * @param saltSource  密码盐
     * @return
     */
    public static String md5(String credentials, String saltSource) {
        return new SimpleHash("MD5", credentials, saltSource, HASH_ITERATIONS).toHex();
    }

    public static String sha256(String credentials, String saltSource) {
        return new SimpleHash("SHA-256", credentials, saltSource, HASH_ITERATIONS).toHex();
    }


    /**
     * 获取随机盐值
     *
     * @param length 字节长度，一个字节2位16进制数表示
     * @return
     */
    public static String getRandomSalt(int length) {
        return new SecureRandomNumberGenerator().nextBytes(length).toHex();
    }

    /**
     * 获取当前 Subject
     *
     * @return Subject
     */
    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }


    /**
     * 验证当前用户是否属于该角色？,使用时与lacksRole 搭配使用
     *
     * @param roleName 角色名
     * @return 属于该角色：true，否则false
     */
    public static boolean hasRole(String roleName) {
        return getSubject() != null && roleName != null
                && roleName.length() > 0 && getSubject().hasRole(roleName);
    }

    /**
     * 与hasRole标签逻辑相反，当用户不属于该角色时验证通过。
     *
     * @param roleName 角色名
     * @return 不属于该角色：true，否则false
     */
    public static boolean lacksRole(String roleName) {
        return !hasRole(roleName);
    }

    /**
     * 验证当前用户是否属于以下任意一个角色。
     *
     * @param roleNames 角色列表
     * @return 属于:true,否则false
     */
    public static boolean hasAnyRoles(String roleNames) {
        boolean hasAnyRole = false;
        Subject subject = getSubject();
        if (subject != null && roleNames != null && roleNames.length() > 0) {
            for (String role : roleNames.split(GlobalConstant.SPLIT_DELIMETER)) {
                if (subject.hasRole(role.trim())) {
                    hasAnyRole = true;
                    break;
                }
            }
        }
        return hasAnyRole;
    }

    /**
     * 验证当前用户是否拥有指定权限,使用时与lacksPermission 搭配使用
     *
     * @param permission 权限名
     * @return 拥有权限：true，否则false
     */
    public static boolean hasPermission(String permission) {
        return getSubject() != null && permission != null
                && permission.length() > 0
                && getSubject().isPermitted(permission);
    }

    /**
     * 与hasPermission标签逻辑相反，当前用户没有制定权限时，验证通过。
     *
     * @param permission 权限名
     * @return 拥有权限：true，否则false
     */
    public static boolean lacksPermission(String permission) {
        return !hasPermission(permission);
    }

    /**
     * 已认证通过的用户，不包含已记住的用户，这是与user标签的区别所在。与notAuthenticated搭配使用
     *
     * @return 通过身份验证：true，否则false
     */
    public static boolean isAuthenticated() {
        return getSubject() != null && getSubject().isAuthenticated();
    }

    /**
     * 未认证通过用户，与authenticated标签相对应。与guest标签的区别是，该标签包含已记住用户。。
     *
     * @return 没有通过身份验证：true，否则false
     */
    public static boolean notAuthenticated() {
        return !isAuthenticated();
    }

    /**
     * 认证通过或已记住的用户。与guset搭配使用。
     *
     * @return 用户：true，否则 false
     */
    public static boolean isUser() {
        return getSubject() != null && getSubject().getPrincipal() != null;
    }


    /**
     * 验证当前用户是否为“访客”，即未认证（包含未记住）的用户。用user搭配使用
     *
     * @return 访客：true，否则false
     */
    public static boolean isGuest() {
        return !isUser();
    }

    /**
     * 输出当前用户信息，通常为登录帐号信息。
     *
     * @return 当前用户信息
     */
    public static String principal() {
        if (getSubject() != null) {
            Object principal = getSubject().getPrincipal();
            return principal.toString();
        }
        return "";
    }

}
