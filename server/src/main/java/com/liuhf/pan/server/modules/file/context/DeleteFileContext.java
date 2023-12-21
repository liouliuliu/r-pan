package com.liuhf.pan.server.modules.file.context;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.List;

/**
 * @author: lhf
 * @date: 2023/12/21 21:47
 * @description
 */
@Data
public class DeleteFileContext implements Serializable {

    private static final long serialVersionUID = -5040051387091567725L;

    /**
     * 要删除的文件ID集合
     */
    private List<Long> fileIdList;

    /**
     * 当前的登录用户ID
     */
    private Long userId;

}