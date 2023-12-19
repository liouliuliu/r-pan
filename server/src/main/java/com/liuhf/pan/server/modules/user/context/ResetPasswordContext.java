package com.liuhf.pan.server.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/18 22:11
 * @description 重置用户密码上下文对象
 */
@Data
public class ResetPasswordContext implements Serializable {

    private static final long serialVersionUID = -96757317621539693L;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户新密码
     */
    private String password;

    /**
     * 重置用户的token
     */
    private String token;
}
