package com.liuhf.pan.server.modules.file.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: lhf
 * @date: 2023/12/12 23:38
 * @description 文件夹删除标识枚举类
 */
@AllArgsConstructor
@Getter
public enum DelFlagEnum {

    /**
     * 未删除
     */
    NO(0),
    /**
     * 已删除
     */
    Yes(1);
    
    private Integer code;
}
