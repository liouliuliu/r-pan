package com.liuhf.pan.server.modules.user;

import cn.hutool.core.lang.Assert;
import com.liuhf.pan.core.exception.RPanBusinessException;
import com.liuhf.pan.core.utils.JwtUtil;
import com.liuhf.pan.server.RPanServerLauncher;
import com.liuhf.pan.server.modules.user.constants.UserConstants;
import com.liuhf.pan.server.modules.user.context.*;
import com.liuhf.pan.server.modules.user.service.IUserService;
import com.liuhf.pan.server.modules.user.vo.UserInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author: lhf
 * @date: 2023/12/13 21:45
 * @description 用户模块单元测试类
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RPanServerLauncher.class)
@Transactional
public class UserTest {

    @Autowired
    private IUserService userService;

    /**
     * 测试成功注册用户信息
     */
    @Test
    public void testRegisterUser() {
        UserRegisterContext context = createUserRegisterContext();

        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);
    }

    /**
     * 测试重复用户名称注册幂等
     */
    @Test(expected = RPanBusinessException.class)
    public void testRegisterDuplicateUsername() {
        UserRegisterContext context = createUserRegisterContext();

        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);
        userService.register(context);
    }

    /**
     * 测试登陆成功
     */
    @Test
    public void loginSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String accessToken = userService.login(userLoginContext);

        Assert.isTrue(StringUtils.isNotBlank(accessToken));
    }

    /**
     * 测试登录失败：用户名不正确
     */
    @Test(expected = RPanBusinessException.class)
    public void wrongUsername() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setUsername(userLoginContext.getUsername() + "_change");
        userService.login(userLoginContext);
    }

    /**
     * 测试登录失败：密码不正确
     */
    @Test(expected = RPanBusinessException.class)
    public void wrongPassword() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        userLoginContext.setPassword(userLoginContext.getPassword() + "_change");
        userService.login(userLoginContext);
    }

    /**
     * 用户成功登出
     */
    @Test
    public void exitSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserLoginContext userLoginContext = createUserLoginContext();
        String accessToken = userService.login(userLoginContext);

        Assert.isTrue(StringUtils.isNotBlank(accessToken));
        Long userId = (Long) JwtUtil.analyzeToken(accessToken, UserConstants.LOGIN_USER_ID);
        userService.exit(userId);
    }

    /**
     * 校验用户名称通过
     */
    @Test
    public void checkUsernameSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername(USERNAME);
        String question = userService.checkUsername(checkUsernameContext);
        Assert.isTrue(StringUtils.isNotBlank(question));
    }


    /**
     * 校验用户名称失败-没有查询到该用户
     */
    @Test(expected = RPanBusinessException.class)
    public void checkUsernameNotExist() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        CheckUsernameContext checkUsernameContext = new CheckUsernameContext();
        checkUsernameContext.setUsername(USERNAME + "_change");
        String question = userService.checkUsername(checkUsernameContext);
        Assert.isTrue(StringUtils.isNotBlank(question));
    }

    /**
     * 校验密保问题答案通过
     */
    @Test
    public void checkAnswerSuccess() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER);
        String token = userService.checkAnswer(checkAnswerContext);

        Assert.isTrue(StringUtils.isNotBlank(token));
    }


    /**
     * 校验用户密保问题答案失败
     */
    @Test(expected = RPanBusinessException.class)
    public void checkAnswerFail() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER + "_change");
        String token = userService.checkAnswer(checkAnswerContext);

        Assert.isTrue(StringUtils.isNotBlank(token));
    }

    /**
     * 正常重置用户密码
     */
    @Test
    public void resetPasswordSuccess(){
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER);
        String token = userService.checkAnswer(checkAnswerContext);

        Assert.isTrue(StringUtils.isNotBlank(token));

        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setUsername(USERNAME);
        resetPasswordContext.setPassword(PASSWORD + "_change");
        resetPasswordContext.setToken(token);

        userService.resetPassword(resetPasswordContext);
    }

    /**
     * 用户重置密码失败 token 异常
     */
    @Test(expected = RPanBusinessException.class)
    public void resetPasswordTokenError(){
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        CheckAnswerContext checkAnswerContext = new CheckAnswerContext();
        checkAnswerContext.setUsername(USERNAME);
        checkAnswerContext.setQuestion(QUESTION);
        checkAnswerContext.setAnswer(ANSWER);
        String token = userService.checkAnswer(checkAnswerContext);

        Assert.isTrue(StringUtils.isNotBlank(token));

        ResetPasswordContext resetPasswordContext = new ResetPasswordContext();
        resetPasswordContext.setUsername(USERNAME);
        resetPasswordContext.setPassword(PASSWORD + "_change");
        resetPasswordContext.setToken(token + "_change");

        userService.resetPassword(resetPasswordContext);
    }

    /**
     * 正常在线修改密码
     */
    @Test
    public void changePasswordSuccess(){
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);
        
        ChangePasswordContext changePasswordContext = new ChangePasswordContext();
        changePasswordContext.setUserId(register);
        changePasswordContext.setOldPassword(PASSWORD);
        changePasswordContext.setNewPassword(PASSWORD + "_change");
        userService.changePassword(changePasswordContext);
    }

    /**
     * 修改密码失败-旧密码错误
     */
    @Test(expected = RPanBusinessException.class)
    public void changePasswordFailByWrongOldPassword(){
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        ChangePasswordContext changePasswordContext = new ChangePasswordContext();
        changePasswordContext.setUserId(register);
        changePasswordContext.setOldPassword(PASSWORD + "_change");
        changePasswordContext.setNewPassword(PASSWORD + "_change");
        userService.changePassword(changePasswordContext);
    }
    
    @Test
    public void testQueryUserInfo(){
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);

        UserInfoVO info = userService.info(register);
        Assert.notNull(info);
    }

    //**************************************************private**************************************************

    private static final String USERNAME = "liuhf";
    private static final String PASSWORD = "12345678";
    private static final String QUESTION = "question";
    private static final String ANSWER = "question";

    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

    private UserLoginContext createUserLoginContext() {
        UserLoginContext context = new UserLoginContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        return context;
    }
}
