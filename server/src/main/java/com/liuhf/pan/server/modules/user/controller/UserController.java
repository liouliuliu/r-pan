package com.liuhf.pan.server.modules.user.controller;

import com.liuhf.pan.core.response.R;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.common.utils.UserIdUtil;
import com.liuhf.pan.server.modules.user.context.UserLoginContext;
import com.liuhf.pan.server.modules.user.context.UserRegisterContext;
import com.liuhf.pan.server.modules.user.converter.UserConverter;
import com.liuhf.pan.server.modules.user.po.UserLoginPO;
import com.liuhf.pan.server.modules.user.po.UserRegisterPO;
import com.liuhf.pan.server.modules.user.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @PostMapping("register")
    public R register(@Validated @RequestBody UserRegisterPO userRegisterPO){
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
    @PostMapping("login")
    public R register(@Validated @RequestBody UserLoginPO userLoginPO){
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
    public R exit(){
        iUserService.exit(UserIdUtil.get());
        return R.success();
    }
    
}
