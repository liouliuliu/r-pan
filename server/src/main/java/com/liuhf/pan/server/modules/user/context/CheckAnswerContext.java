package com.liuhf.pan.server.modules.user.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/18 22:11
 * @description 校验密保答案PO对象
 */
@Data
public class CheckAnswerContext implements Serializable {

    private static final long serialVersionUID = 2425571749077407582L;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 密保问题
     */
    private String question;

    /**
     * 密保答案
     */
    private String answer;
}
