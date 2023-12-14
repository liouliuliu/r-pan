package com.liuhf.pan.server.common.utils;

import com.liuhf.pan.core.constants.RPanConstants;

import java.util.Objects;

/**
 * @author: lhf
 * @date: 2023/12/14 22:10
 * @description 用户ID存储工具类
 */
public class UserIdUtil {

    private static ThreadLocal<Long> threadLocal = new ThreadLocal<>();

    /**
     * 设置当前线程的用户ID
     */
    public static void set(Long userId) {
        threadLocal.set(userId);
    }

    /**
     * 获取当前线程的用户ID
     */
    public static Long get() {
        Long userId = threadLocal.get();
        if (Objects.isNull(userId)) {
            return RPanConstants.ZERO_LONG;
        }
        return userId;
    }
}
