package com.liuhf.pan.server.modules.user.converter;

import com.liuhf.pan.server.modules.user.context.UserLoginContext;
import com.liuhf.pan.server.modules.user.context.UserRegisterContext;
import com.liuhf.pan.server.modules.user.entity.RPanUser;
import com.liuhf.pan.server.modules.user.po.UserLoginPO;
import com.liuhf.pan.server.modules.user.po.UserRegisterPO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author: lhf
 * @date: 2023/12/12 22:15
 * @description 用户模块实体转化工具类
 */
@Mapper(componentModel = "spring")
public interface UserConverter {

    /**
     * UserRegisterPO 转化成 UserRegisterContext
     * @param userRegisterPO
     * @return
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    /**
     * UserLoginPO 转化成 UserLoginContext
     * @param userLoginPO
     * @return
     */
    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);
    
    
    @Mapping(target = "password",ignore = true)
    RPanUser userRegisterContext2RPanUser(UserRegisterContext userRegisterContext);
}
