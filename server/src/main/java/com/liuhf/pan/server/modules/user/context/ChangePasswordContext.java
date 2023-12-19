package com.liuhf.pan.server.modules.user.context;

import com.liuhf.pan.server.modules.user.entity.RPanUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/19 21:46
 * @description 用户在线修改密码上下文实体
 */
@Data
public class ChangePasswordContext implements Serializable {

    private static final long serialVersionUID = 7942303367715676591L;

    /**
     * 当前登录的用户 ID
     */
    private Long userId;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 当前登录用户的实体信息
     */
    private RPanUser entity;
    
}
