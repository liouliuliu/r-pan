package com.liuhf.pan.server.modules.file.service;

import com.liuhf.pan.server.modules.file.context.CreateFolderContext;
import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author 52300
* @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service
* @createDate 2023-12-07 22:33:53
*/
public interface IUserFileService extends IService<RPanUserFile> {

    /**
     * 创建文件夹信息
     */
    Long createFolder(CreateFolderContext createFolderContext);

}
