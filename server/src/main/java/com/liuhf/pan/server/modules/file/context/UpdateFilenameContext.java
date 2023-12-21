package com.liuhf.pan.server.modules.file.context;

import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/21 21:14
 * @description
 */
@Data
public class UpdateFilenameContext implements Serializable {

    private static final long serialVersionUID = 2030522024482551370L;
    /**
     * 要更新的文件ID
     */
    private Long fileId;

    /**
     * 新的文件名称
     */
    private String newFilename;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

    /**
     * 要更新的文件记录实体
     */
    private RPanUserFile entity;
}