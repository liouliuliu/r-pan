package com.liuhf.pan.server.modules.file.service;

import com.liuhf.pan.server.modules.file.context.CreateFolderContext;
import com.liuhf.pan.server.modules.file.context.DeleteFileContext;
import com.liuhf.pan.server.modules.file.context.QueryFileListContext;
import com.liuhf.pan.server.modules.file.context.UpdateFilenameContext;
import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuhf.pan.server.modules.file.vo.RPanUserFileVO;

import java.util.List;

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

    /**
     * 获取用户根文件夹信息实体
     */
    RPanUserFile getUserRootFile(Long userId);

    /**
     * 查询用户的文件列表
     */
    List<RPanUserFileVO> getFileList(QueryFileListContext context);

    /**
     * 文件夹重命名
     */
    void updateFilename(UpdateFilenameContext context);

    /**
     * 批量删除用户文件
     */
    void deleteFile(DeleteFileContext context);
}
