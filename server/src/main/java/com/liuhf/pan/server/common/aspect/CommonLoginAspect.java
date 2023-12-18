package com.liuhf.pan.server.common.aspect;

import com.liuhf.pan.cache.core.constants.CacheConstants;
import com.liuhf.pan.core.response.R;
import com.liuhf.pan.core.response.ResponseCode;
import com.liuhf.pan.core.utils.JwtUtil;
import com.liuhf.pan.server.common.annotation.LoginIgnore;
import com.liuhf.pan.server.common.utils.UserIdUtil;
import com.liuhf.pan.server.modules.user.constants.UserConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author: lhf
 * @date: 2023/12/18 20:48
 * @description 统一的登录拦截校验切面逻辑实现类
 */
@Component
@Aspect
@Slf4j
public class CommonLoginAspect {

    /**
     * 登录认证参数名称
     */
    private static final String LOGIN_AUTH_PARAM_NAME = "authorization";

    /**
     * 请求头登录认证 key
     */
    private static final String LOGIN_AUTH_REQUEST_HEADER_NAME = "Authorization";

    /**
     * 切点表达式
     */
    private final static String POINT_CUT = "execution(* com.liuhf.pan.server.modules.*.controller..*(..))";

    @Autowired
    private CacheManager cacheManager;

    /**
     * 切点模板方法
     */
    @Pointcut(value = POINT_CUT)
    public void loginAuth() {

    }

    /**
     * 切点的环绕增强逻辑
     * <p>
     * 1、需要判断需不需要校验分享token信息
     * 2、校验登录信息：
     * a、获取token 从请求头或者参数
     * b、解析token
     * c、解析的shareId存入线程上下文，供下游使用
     */
    @Around("loginAuth()")
    public Object loginAuthAround(ProceedingJoinPoint pjp) throws Throwable {
        if (checkNeedCheckLoginInfo(pjp)) {
            // 登录信息校验流程
            ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            assert requestAttributes != null;
            HttpServletRequest request = requestAttributes.getRequest();
            StringBuffer requestURL = request.getRequestURL();
            log.info("成功拦截到请求，URI为：{}", requestURL);
            if (!checkAndSaveUserId(request)) {
                log.warn("成功拦截到请求，URI为：{}. 检测到用户未登录,将跳转到登录页面", requestURL);
                return R.fail(ResponseCode.NEED_LOGIN);
            }
            log.info("成功拦截到请求，URI为：{},请求通过!", requestURL);
        }
        return pjp.proceed();
    }

    /**
     * 校验 token 并提取 userId
     */
    private boolean checkAndSaveUserId(HttpServletRequest request) {
        String accessToken = request.getHeader(LOGIN_AUTH_REQUEST_HEADER_NAME);
        if (StringUtils.isBlank(accessToken)) {
            accessToken = request.getParameter(LOGIN_AUTH_PARAM_NAME);
        }
        if (StringUtils.isBlank(accessToken)) {
            return false;
        }
        Object userId = JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        if (Objects.isNull(userId)) {
            return false;
        }
        Cache cache = cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
        assert cache != null;
        Object redisAccessToken = cache.get(UserConstants.USER_LOGIN_PREFIX + userId);
        if (Objects.isNull(redisAccessToken)) {
            return false;
        }
        if (Objects.equals(accessToken, redisAccessToken)) {
            saveUserId(userId);
            return true;
        }
        return false;
    }

    /**
     * 保存用户 ID 到线程上下文中
     */
    private void saveUserId(Object userId) {
        UserIdUtil.set(Long.valueOf(String.valueOf(userId)));
    }

    /**
     * 校验是否需要校验登录信息
     *
     * @return true 需要校验登录信息 false 不需要
     */
    private boolean checkNeedCheckLoginInfo(ProceedingJoinPoint pjp) {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        Method method = methodSignature.getMethod();
        return !method.isAnnotationPresent(LoginIgnore.class);
    }
}
