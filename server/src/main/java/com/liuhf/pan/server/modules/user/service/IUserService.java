package com.liuhf.pan.server.modules.user.service;

import com.liuhf.pan.server.modules.user.context.*;
import com.liuhf.pan.server.modules.user.entity.RPanUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuhf.pan.server.modules.user.vo.UserInfoVO;

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

    /**
     * 用户忘记密码-校验用户名
     * @param checkUsernameContext
     * @return 密保问题
     */
    String checkUsername(CheckUsernameContext checkUsernameContext);

    /**
     * 用户忘记密码-校验密保答案
     * @param checkAnswerContext
     * @return
     */
    String checkAnswer(CheckAnswerContext checkAnswerContext);

    /**
     * 重置用户密码
     * @param resetPasswordContext
     */
    void resetPassword(ResetPasswordContext resetPasswordContext);

    /**
     * 用户在线修改密码
     * @param changePasswordContext
     */
    void changePassword(ChangePasswordContext changePasswordContext);

    /**
     * 查询用户登录的基本信息
     * @param aLong
     * @return
     */
    UserInfoVO info(Long userId);
}
