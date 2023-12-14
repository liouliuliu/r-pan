package com.liuhf.pan.server.modules.file.context;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: lhf
 * @date: 2023/12/12 22:54
 * @description 创建文件夹上下文实体
 */
@Data
public class CreateFolderContext implements Serializable {
    
    private static final long serialVersionUID = 194449161580380474L;

    /**
     * 父文件夹id
     */
    private Long parentId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件夹名称
     */
    private String folderName;
}
