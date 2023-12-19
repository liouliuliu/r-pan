package com.liuhf.pan.server.modules.user.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.liuhf.pan.web.serializer.IdEncryptSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/19 22:13
 * @description
 */
@ApiModel(value = "用户基本信息实体")
@Data
public class UserInfoVO implements Serializable {
    private static final long serialVersionUID = 6402830677483899492L;

    @ApiModelProperty("用户名称")
    private String username;

    @ApiModelProperty("用户根目录的加密ID")
    @JsonSerialize(using = IdEncryptSerializer.class)
    private Long rootFileId;

    @ApiModelProperty("用户根目录名称")
    private String rootFilename;
}
