package com.liuhf.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuhf.pan.core.utils.FileUtil;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.modules.file.context.FileSaveContext;
import com.liuhf.pan.server.modules.file.entity.RPanFile;
import com.liuhf.pan.server.modules.file.po.QueryRealFileListContext;
import com.liuhf.pan.server.modules.file.service.IFileService;
import com.liuhf.pan.server.modules.file.mapper.RPanFileMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 52300
* @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2023-12-07 22:33:53
*/
@Service
public class FileServiceImpl extends ServiceImpl<RPanFileMapper, RPanFile> implements IFileService {

    /**
     * 根据条件查询用户的实际文件列表
     *
     */
    @Override
    public List<RPanFile> getFileList(QueryRealFileListContext context) {
        Long userId = context.getUserId();
        String identifier = context.getIdentifier();
        LambdaQueryWrapper<RPanFile> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Objects.nonNull(userId), RPanFile::getCreateUser, userId);
        queryWrapper.eq(StringUtils.isNotBlank(identifier), RPanFile::getIdentifier, identifier);
        return list(queryWrapper);
    }

    /**
     * 上传单文件并保存实体记录
     * 1.上传单文件
     * 2.保存实体记录
     */
    @Override
    public void saveFile(FileSaveContext context) {
        storeMultipartFile(context);
        RPanFile record = doSaveFile(context.getFilename(),context.getRealPath(),context.getTotalSize(),
                context.getIdentifier(),context.getUserId());
        context.setRecord(record);
    }

    //**************************************************private**************************************************

    /**
     * 保存实体文件记录
     */
    private RPanFile doSaveFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        RPanFile record = assembleRPanFile(filename, realPath, totalSize, identifier, userId);
        if (!save(record)){
            // TODO 删除已上传的物理文件
        }
        return record;
    }

    /**
     * 拼装文件实体对象
     */
    private RPanFile assembleRPanFile(String filename, String realPath, Long totalSize, String identifier, Long userId) {
        RPanFile record = new RPanFile();
        record.setFileId(IdUtil.get());
        record.setFilename(filename);
        record.setRealPath(realPath);
        record.setFileSize(String.valueOf(totalSize));
        record.setFileSizeDesc(FileUtil.byteCountToDisplaySize(totalSize));
        record.setFileSuffix(FileUtil.getFileSuffix(filename));
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        return record;
    }

    /**
     * 上传单文件，委托存储引擎实现
     */
    private void storeMultipartFile(FileSaveContext context) {
        
    }
}




