package com.liuhf.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuhf.pan.core.exception.RPanBusinessException;
import com.liuhf.pan.core.utils.FileUtil;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.modules.file.context.FileChunkMergeAndSaveContext;
import com.liuhf.pan.server.modules.file.context.FileSaveContext;
import com.liuhf.pan.server.modules.file.entity.RPanFile;
import com.liuhf.pan.server.modules.file.entity.RPanFileChunk;
import com.liuhf.pan.server.modules.file.po.QueryRealFileListContext;
import com.liuhf.pan.server.modules.file.service.IFileChunkService;
import com.liuhf.pan.server.modules.file.service.IFileService;
import com.liuhf.pan.server.modules.file.mapper.RPanFileMapper;
import com.liuhf.pan.storage.engine.core.StorageEngine;
import com.liuhf.pan.storage.engine.core.context.MergeFileContext;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
* @author 52300
* @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2023-12-07 22:33:53
*/
@Service
public class FileServiceImpl extends ServiceImpl<RPanFileMapper, RPanFile> implements IFileService {
    
    @Autowired
    private IFileChunkService fileChunkService;

    @Autowired
    private StorageEngine storageEngine;

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

    /**
     * 合并物理文件并保存物理文件记录
     * 1.委托存储引擎合并文件分片
     * 2.保存物理文件记录
     */
    @Override
    public void mergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context) {
        doMergeFileChunkAndSaveFile(context);
        RPanFile record = doSaveFile(context.getFilename(), context.getRealPath(), context.getTotalSize(), context.getIdentifier(), context.getUserId());
        context.setRecord(record);
    }

    //**************************************************private**************************************************

    /**
     * 委托文件存储引擎合并文件分片
     * 1.查询文件分片的记录
     * 2.根据文件分片的记录去合并物理文件
     * 3.删除文件分片记录
     * 4.封装合并文件的真实存储路径到上下文信息中
     */
    private void doMergeFileChunkAndSaveFile(FileChunkMergeAndSaveContext context) {
        QueryWrapper<RPanFileChunk> queryWrapper = Wrappers.query();
        queryWrapper.eq("identifier",context.getIdentifier());
        queryWrapper.eq("create_user",context.getUserId());
        queryWrapper.ge("expiration_time", new Date());
        List<RPanFileChunk> chunkRecoredList = fileChunkService.list(queryWrapper);

        if (CollectionUtils.isEmpty(chunkRecoredList)) {
            throw new RPanBusinessException("该文件未找到分片记录！");
        }
        List<String> realPathList = chunkRecoredList.stream()
                .sorted(Comparator.comparing(RPanFileChunk::getChunkNumber))
                .map(RPanFileChunk::getRealPath)
                .collect(Collectors.toList());
        try {
            // 委托存储引擎去合并文件分片
            MergeFileContext mergeFileContext = new MergeFileContext();
            mergeFileContext.setFilename(context.getFilename());
            mergeFileContext.setIdentifier(context.getIdentifier());
            mergeFileContext.setUserId(context.getUserId());
            mergeFileContext.setRealPathList(realPathList);
            storageEngine.mergeFile(mergeFileContext);
            // 封装实体文件的真实存储路径
            context.setRealPath(mergeFileContext.getRealPath());
        } catch (IOException e) {
            throw new RPanBusinessException("文件分片合并失败");
        }

        List<Long> fileChunkRecordList = chunkRecoredList.stream().map(RPanFileChunk::getId).collect(Collectors.toList());
        fileChunkService.removeByIds(fileChunkRecordList);
    }

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




