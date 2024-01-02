package com.liuhf.pan.server.modules.file.service;

import com.liuhf.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.liuhf.pan.server.modules.file.context.FileSaveContext;
import com.liuhf.pan.server.modules.file.entity.RPanFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liuhf.pan.server.modules.file.po.QueryRealFileListContext;

import java.util.List;

/**
 * @author 52300
 * @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service
 * @createDate 2023-12-07 22:33:53
 */
public interface IFileService extends IService<RPanFile> {

    /**
     * 根据条件查询用户的实际文件列表
     */
    List<RPanFile> getFileList(QueryRealFileListContext context);

    /**
     * 上传单文件并保存实体记录
     */
    void saveFile(FileSaveContext context);

    /**
     * 合并物理文件并保存物理文件记录
     */
    void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context);
}
