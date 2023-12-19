package com.liuhf.pan.server.modules.user.controller;

import com.liuhf.pan.core.response.R;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.common.annotation.LoginIgnore;
import com.liuhf.pan.server.common.utils.UserIdUtil;
import com.liuhf.pan.server.modules.user.context.*;
import com.liuhf.pan.server.modules.user.converter.UserConverter;
import com.liuhf.pan.server.modules.user.po.*;
import com.liuhf.pan.server.modules.user.service.IUserService;
import com.liuhf.pan.server.modules.user.vo.UserInfoVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author: lhf
 * @date: 2023/12/12 22:03
 * @description 用户模块的控制器实体
 */
@RestController
@RequestMapping("user")
@Api(tags = "用户模块")
public class UserController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private UserConverter userConverter;

    @ApiOperation(
            value = "用户注册接口",
            notes = "该接口提供了用户注册功能，实现了幂等性逻辑",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LoginIgnore
    @PostMapping("register")
    public R register(@Validated @RequestBody UserRegisterPO userRegisterPO) {
        UserRegisterContext userRegisterContext = userConverter.userRegisterPO2UserRegisterContext(userRegisterPO);
        Long userId = iUserService.register(userRegisterContext);
        return R.data(IdUtil.encrypt(userId));
    }


    @ApiOperation(
            value = "用户登录接口",
            notes = "该接口提供了用户登录功能，成功登录后，返回有时效性的 accessToken",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LoginIgnore
    @PostMapping("login")
    public R register(@Validated @RequestBody UserLoginPO userLoginPO) {
        UserLoginContext userLoginContext = userConverter.userLoginPO2UserLoginContext(userLoginPO);
        String accessToken = iUserService.login(userLoginContext);
        return R.data(accessToken);
    }

    @ApiOperation(
            value = "用户登出接口",
            notes = "该接口提供了用户登出功能",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("exit")
    public R exit() {
        iUserService.exit(UserIdUtil.get());
        return R.success();
    }

    @ApiOperation(
            value = "用户忘记密码-校验用户名",
            notes = "该接口提供了 用户忘记密码-校验用户名",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LoginIgnore
    @PostMapping("username/check")
    public R checkUsername(@Validated @RequestBody CheckUsernamePO checkUsernamePO) {
        CheckUsernameContext checkUsernameContext = userConverter.checkUsernamePO2CheckUsernameContext(checkUsernamePO);
        String question = iUserService.checkUsername(checkUsernameContext);
        return R.data(question);
    }

    @ApiOperation(
            value = "用户忘记密码-校验密保答案",
            notes = "该接口提供了 用户忘记密码-校验密保答案",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @LoginIgnore
    @PostMapping("answer/check")
    public R checkAnswer(@Validated @RequestBody CheckAnswerPO checkAnswerPO) {
        CheckAnswerContext checkAnswerContext = userConverter.checkAnswerPO2CheckAnswerContext(checkAnswerPO);
        String token = iUserService.checkAnswer(checkAnswerContext);
        return R.data(token);
    }

    @ApiOperation(
            value = "用户忘记密码-重置新密码",
            notes = "该接口提供了 用户忘记密码-重置新密码",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("password/reset")
    @LoginIgnore
    public R resetPassword(@Validated @RequestBody ResetPasswordPO resetPasswordPO) {
        ResetPasswordContext resetPasswordContext = userConverter.resetPasswordPO2ResetPasswordContext(resetPasswordPO);
        iUserService.resetPassword(resetPasswordContext);
        return R.success();
    }

    @ApiOperation(
            value = "用户在线修改密码",
            notes = "该接口提供了 用户在线修改密码",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PostMapping("password/change")
    public R changePassword(@Validated @RequestBody ChangePasswordPO changePasswordPO){
        ChangePasswordContext changePasswordContext = userConverter.changePasswordPO2ChangePasswordContext(changePasswordPO);
        changePasswordContext.setUserId(UserIdUtil.get());
        iUserService.changePassword(changePasswordContext);
        return R.success();
    }

    @ApiOperation(
            value = "查询用户登录的基本信息",
            notes = "该接口提供了 查询用户登录的基本信息",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @GetMapping("/")
    public R<UserInfoVO> info(){
        UserInfoVO userInfoVO = iUserService.info(UserIdUtil.get());        
        return R.data(userInfoVO);
    }
}
