package com.liuhf.pan.core.utils;

import com.liuhf.pan.core.constants.RPanConstants;
import org.apache.commons.lang3.StringUtils;

/**
 * @author: lhf
 * @date: 2023/12/21 22:54
 * @description 文件相关的工具类
 */
public class FileUtil {

    /**
     * 获取文件的后缀
     *
     * @param filename
     * @return
     */
    public static String getFileSuffix(String filename) {
        if (StringUtils.isBlank(filename) || filename.lastIndexOf(RPanConstants.POINT_STR) == RPanConstants.MINUS_ONE_INT) {
            return RPanConstants.EMPTY_STR;
        }
        return filename.substring(filename.lastIndexOf(RPanConstants.POINT_STR)).toLowerCase();
    }
    
}
