package com.tfx0one.modules.app.interceptor;

import com.tfx0one.common.exception.CommonException;
import com.tfx0one.modules.app.annotation.Login;
import com.tfx0one.modules.app.resolver.LoginUserHandlerMethodArgumentResolver;
import com.tfx0one.modules.app.utils.AppJWTUtils;
import io.jsonwebtoken.Claims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 权限(Token)验证
 *
 */
@Component
public class AuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Autowired
    private AppJWTUtils appJwtUtils;

    public static final String USER_KEY = "userId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        Login annotation;
        if (handler instanceof HandlerMethod) {
            if (((HandlerMethod) handler).getMethodAnnotation(Login.class) == null) {
                return true;
            }
        } else {
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(appJwtUtils.getHeader());
        if (StringUtils.isBlank(token)) {
            token = request.getParameter(appJwtUtils.getHeader());
        }

        //凭证为空
        if (StringUtils.isBlank(token)) {
            throw new CommonException(appJwtUtils.getHeader() + "不能为空", HttpStatus.UNAUTHORIZED.value());
        }

        Claims claims = appJwtUtils.getClaimByToken(token);
        if (claims == null || appJwtUtils.isTokenExpired(claims.getExpiration())) {
            throw new CommonException(appJwtUtils.getHeader() + "失效，请重新登录", HttpStatus.UNAUTHORIZED.value());
        }

        /**
         *  设置userId到request里，后续 {@link LoginUserHandlerMethodArgumentResolver} 根据userId，获取用户信息
         **/
        request.setAttribute(USER_KEY, Long.parseLong(claims.getSubject()));

        return true;
    }
}
