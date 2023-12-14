package com.liuhf.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author: lhf
 * @date: 2023/12/12 23:15
 * @description 文件夹标识枚举类
 */
@AllArgsConstructor
@Getter
public enum FolderFlagEnum {

    /**
     * 非文件夹
     */
    NO(0),
    /**
     * 是文件夹
     */
    YES(1);
    
    private Integer code;
    
}
