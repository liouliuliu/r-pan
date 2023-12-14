package com.liuhf.pan.server.modules.user.context;

import com.liuhf.pan.server.modules.user.entity.RPanUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/12 22:13
 * @description 用户注册业务的上下文实体对象
 */
@Data
public class UserRegisterContext implements Serializable {
    
    private static final long serialVersionUID = 6711154660348476227L;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;

    /**
     * 用户实体对象
     */
    private RPanUser entity;
}
