package com.liuhf.pan.server.modules.user.context;

import com.liuhf.pan.server.modules.user.entity.RPanUser;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/12 22:13
 * @description 用户登录业务的上下文实体对象
 */
@Data
public class UserLoginContext implements Serializable {

    private static final long serialVersionUID = 9125114946369434203L;
    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 用户实体对象
     */
    private RPanUser entity;

    /**
     * 登陆成功之后的凭证信息
     */
    private String accessToken;
}
