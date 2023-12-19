package com.liuhf.pan.server.modules.user.context;

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
@Data
public class CheckUsernameContext implements Serializable {


    private static final long serialVersionUID = -4569934538340789489L;

    /**
     * 用户名称
     */
    private String username;
}
