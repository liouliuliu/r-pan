package com.liuhf.pan.server.modules.user.service;

import com.liuhf.pan.server.modules.user.context.UserLoginContext;
import com.liuhf.pan.server.modules.user.context.UserRegisterContext;
import com.liuhf.pan.server.modules.user.entity.RPanUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 52300
* @description 针对表【r_pan_user(用户信息表)】的数据库操作Service
* @createDate 2023-12-07 22:31:49
*/
public interface IUserService extends IService<RPanUser> {

    /**
     * 用户注册业务
     */
    Long register(UserRegisterContext userRegisterContext);

    /**
     * 用户登录业务
     */
    String login(UserLoginContext userLoginContext);

    /**
     * 用户退出登录
     */
    void exit(Long userId);
}
