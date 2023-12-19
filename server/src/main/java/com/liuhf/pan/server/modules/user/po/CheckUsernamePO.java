package com.liuhf.pan.server.modules.user.po;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/18 22:11
 * @description 校验用户名参数PO对象
 */
@ApiModel(value = "用户忘记密码-校验用户名参数")
@Data
public class CheckUsernamePO implements Serializable {

    private static final long serialVersionUID = -7360329690632021827L;
    
    @ApiModelProperty(value = "用户名",required = true)
    @NotBlank(message = "用户名称不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,16}$", message = "请输入6-16位只包含数字和字母的用户名")
    private String username;
}
