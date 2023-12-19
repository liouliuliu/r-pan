package com.liuhf.pan.server.modules.user.converter;

import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import com.liuhf.pan.server.modules.user.context.*;
import com.liuhf.pan.server.modules.user.entity.RPanUser;
import com.liuhf.pan.server.modules.user.po.*;
import com.liuhf.pan.server.modules.user.vo.UserInfoVO;
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
     */
    UserRegisterContext userRegisterPO2UserRegisterContext(UserRegisterPO userRegisterPO);

    /**
     * UserLoginPO 转化成 UserLoginContext
     */
    UserLoginContext userLoginPO2UserLoginContext(UserLoginPO userLoginPO);

    /**
     * UserRegisterContext 转化为 RPanUser
     */
    @Mapping(target = "password", ignore = true)
    RPanUser userRegisterContext2RPanUser(UserRegisterContext userRegisterContext);

    /**
     * CheckUsernamePO 转化为 CheckUsernameContext
     */
    CheckUsernameContext checkUsernamePO2CheckUsernameContext(CheckUsernamePO checkUsernamePO);

    /**
     * CheckAnswerPO 转化为 CheckAnswerContext
     */
    CheckAnswerContext checkAnswerPO2CheckAnswerContext(CheckAnswerPO checkAnswerPO);

    /**
     * ResetPasswordPO 转化为 ResetPasswordContext
     */
    ResetPasswordContext resetPasswordPO2ResetPasswordContext(ResetPasswordPO resetPasswordPO);

    /**
     * ChangePasswordPO 转化为 ChangePasswordContext
     */
    ChangePasswordContext changePasswordPO2ChangePasswordContext(ChangePasswordPO changePasswordPO);


    /**
     * 拼装用户基本信息返回实体
     *
     * @param rPanUser
     * @param rPanUserFile
     * @return
     */
    @Mapping(source = "rPanUser.username", target = "username")
    @Mapping(source = "rPanUserFile.fileId", target = "rootFileId")
    @Mapping(source = "rPanUserFile.filename", target = "rootFilename")
    UserInfoVO assembleUserInfoVO(RPanUser rPanUser, RPanUserFile rPanUserFile);
}
