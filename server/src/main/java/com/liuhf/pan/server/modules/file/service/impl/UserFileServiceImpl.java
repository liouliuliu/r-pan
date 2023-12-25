package com.liuhf.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuhf.pan.core.constants.RPanConstants;
import com.liuhf.pan.core.exception.RPanBusinessException;
import com.liuhf.pan.core.utils.FileUtil;
import com.liuhf.pan.core.utils.FileUtils;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.common.event.file.DeleteFileEvent;
import com.liuhf.pan.server.modules.file.constants.FileConstants;
import com.liuhf.pan.server.modules.file.context.*;
import com.liuhf.pan.server.modules.file.entity.RPanFile;
import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import com.liuhf.pan.server.modules.file.enums.DelFlagEnum;
import com.liuhf.pan.server.modules.file.enums.FileTypeEnum;
import com.liuhf.pan.server.modules.file.enums.FolderFlagEnum;
import com.liuhf.pan.server.modules.file.service.IFileService;
import com.liuhf.pan.server.modules.file.service.IUserFileService;
import com.liuhf.pan.server.modules.file.mapper.RPanUserFileMapper;
import com.liuhf.pan.server.modules.file.vo.RPanUserFileVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author 52300
 * @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2023-12-07 22:33:53
 */
@Service(value = "userFileService")
public class UserFileServiceImpl extends ServiceImpl<RPanUserFileMapper, RPanUserFile> implements IUserFileService, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Autowired
    private IFileService fileService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    /**
     * 创建文件夹信息
     */
    @Override
    public Long createFolder(CreateFolderContext createFolderContext) {
        return saveUserFile(createFolderContext.getParentId(), createFolderContext.getFolderName(),
                FolderFlagEnum.YES, null, null, createFolderContext.getUserId(), null);
    }

    /**
     * 获取用户根文件夹信息实体
     */
    @Override
    public RPanUserFile getUserRootFile(Long userId) {
        QueryWrapper<RPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.eq("parent_id", FileConstants.TOP_PARENT_ID);
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        queryWrapper.eq("folder_flag", FolderFlagEnum.YES.getCode());
        return getOne(queryWrapper);
    }

    /**
     * 查询用户的文件列表
     */
    @Override
    public List<RPanUserFileVO> getFileList(QueryFileListContext context) {
        return baseMapper.selectFileList(context);
    }

    /**
     * 文件夹重命名
     */
    @Override
    public void updateFilename(UpdateFilenameContext context) {
        checkUpdateFilenameCondition(context);
        doUpdateFilename(context);
    }

    /**
     * 批量删除用户文件
     */
    @Override
    public void deleteFile(DeleteFileContext context) {
        checkFileDeleteCondition(context);
        doDeleteFile(context);
        afterFileDelete(context);
    }

    /**
     * 文件秒传
     * 1、通过文件唯一标识，查找对应的实体文件记录
     * 2、如果没查到，直接返回秒传失败
     * 3、查到，直接挂载关联关系，返回秒传成功
     */
    @Override
    public boolean secUpload(SecUploadFileContext context) {
        RPanFile record = getFileByUserIdAndIdentifier(context.getUserId(), context.getIdentifier());
        if (Objects.isNull(record)) {
            return false;
        }
        saveUserFile(context.getParentId(),
                context.getFilename(),
                FolderFlagEnum.NO,
                FileTypeEnum.getFileTypeCode(FileUtils.getFileSuffix(context.getFilename())),
                record.getFileId(),
                context.getUserId(),
                record.getFileSizeDesc());
        return true;
    }

    //**************************************************private**************************************************

    /**
     *
     */
    private RPanFile getFileByUserIdAndIdentifier(Long userId, String identifier) {
        QueryWrapper qw = Wrappers.query();
        qw.eq("create_user", userId);
        qw.eq("identifier", identifier);
        List<RPanFile> records = fileService.list(qw);
        if (CollectionUtils.isEmpty(records)) {
            return null;
        }
        return records.get(RPanConstants.ZERO_INT);
    }

    /**
     * 文件删除的后置操作
     * 1、对外发布文件删除的事件
     */
    private void afterFileDelete(DeleteFileContext context) {
        DeleteFileEvent deleteFileEvent = new DeleteFileEvent(this, context.getFileIdList());
        applicationContext.publishEvent(deleteFileEvent);
    }

    /**
     * 执行文件删除
     */
    private void doDeleteFile(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        UpdateWrapper<RPanUserFile> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in("file_id", fileIdList);
        updateWrapper.set("del_flag", DelFlagEnum.Yes.getCode());
        updateWrapper.set("update_time", new Date());

        if (!update(updateWrapper)) {
            throw new RPanBusinessException("文件删除失败");
        }
    }

    /**
     * 删除文件的前置校验
     */
    private void checkFileDeleteCondition(DeleteFileContext context) {
        List<Long> fileIdList = context.getFileIdList();
        List<RPanUserFile> rPanUserFiles = listByIds(fileIdList);
        if (rPanUserFiles.size() != fileIdList.size()) {
            throw new RPanBusinessException("存在不合法的文件记录");
        }

        Set<Long> fileIdSet = rPanUserFiles.stream().map(RPanUserFile::getFileId).collect(Collectors.toSet());
        int oldSize = fileIdSet.size();
        fileIdSet.addAll(fileIdList);
        int newSize = fileIdSet.size();

        if (oldSize != newSize) {
            throw new RPanBusinessException("存在不合法的文件记录");
        }

        Set<Long> userIdSet = rPanUserFiles.stream().map(RPanUserFile::getUserId).collect(Collectors.toSet());
        if (userIdSet.size() != 1) {
            throw new RPanBusinessException("存在不合法的文件记录");
        }

        Long dbUserId = userIdSet.stream().findFirst().get();
        if (!Objects.equals(dbUserId, context.getUserId())) {
            throw new RPanBusinessException("当前登录用户没有删除文件权限");
        }
    }

    /**
     * 执行文件重命名
     */
    private void doUpdateFilename(UpdateFilenameContext context) {
        RPanUserFile entity = context.getEntity();
        entity.setFilename(context.getNewFilename());
        entity.setUpdateUser(context.getUserId());
        entity.setUpdateTime(new Date());
        if (!updateById(entity)) {
            throw new RPanBusinessException("文件重命名失败");
        }
    }

    /**
     * 更新文件名称的条件校验
     */
    private void checkUpdateFilenameCondition(UpdateFilenameContext context) {
        Long fileId = context.getFileId();
        RPanUserFile entity = getById(fileId);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("该文件ID无效");
        }
        if (!Objects.equals(entity.getUserId(), context.getUserId())) {
            throw new RPanBusinessException("当前登录用户没有修改该文件名称的权限");
        }
        if (Objects.equals(entity.getFilename(), context.getNewFilename())) {
            throw new RPanBusinessException("请换一个新的文件名称来修改");
        }
        QueryWrapper<RPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("filename", context.getNewFilename());
        int count = count(queryWrapper);
        if (count > 0) {
            throw new RPanBusinessException("该文件夹已被占用");
        }
        context.setEntity(entity);
    }

    /**
     * 保存用户文件的映射记录
     */
    private Long saveUserFile(Long parentId, String fileName, FolderFlagEnum folderFlagEnum,
                              Integer fileType, Long realFileId, Long userId, String fileSizeDesc) {
        RPanUserFile entity = assembleRPanFUserFile(parentId, userId, fileName, folderFlagEnum, fileType, realFileId, fileSizeDesc);
        if (!(save(entity))) {
            throw new RPanBusinessException("保存文件信息失败！");
        }
        return entity.getFileId();
    }

    /**
     * 用户文件映射关系实体转化
     * 1、构建并填充实体
     * 2、处理文件命名一致问题
     */
    private RPanUserFile assembleRPanFUserFile(Long parentId, Long userId, String fileName, FolderFlagEnum folderFlagEnum, Integer fileType, Long realFileId, String fileSizeDesc) {
        RPanUserFile entity = new RPanUserFile();
        entity.setFileId(IdUtil.get());
        entity.setUserId(userId);
        entity.setParentId(parentId);
        entity.setRealFileId(realFileId);
        entity.setFilename(fileName);
        entity.setFolderFlag(folderFlagEnum.getCode());
        entity.setFileSizeDesc(fileSizeDesc);
        entity.setFileType(fileType);
        entity.setDelFlag(DelFlagEnum.NO.getCode());
        entity.setCreateUser(userId);
        entity.setCreateTime(new Date());
        entity.setUpdateUser(userId);
        entity.setUpdateTime(new Date());

        handleDuplicateFilename(entity);

        return entity;
    }

    /**
     * 处理用户重复名称
     */
    private void handleDuplicateFilename(RPanUserFile entity) {
        String filename = entity.getFilename(),
                newFilenameWithoutSuffix,
                newFilenameSuffix;
        int newFilenamePointPosition = filename.lastIndexOf(RPanConstants.POINT_STR);
        if (newFilenamePointPosition == RPanConstants.MINUS_ONE_INT) {
            newFilenameWithoutSuffix = filename;
            newFilenameSuffix = StringUtils.EMPTY;
        } else {
            newFilenameWithoutSuffix = filename.substring(RPanConstants.ZERO_INT, newFilenamePointPosition);
            newFilenameSuffix = filename.replace(newFilenameWithoutSuffix, StringUtils.EMPTY);
        }
        int count = getDuplicateFilename(entity, newFilenameWithoutSuffix);

        if (count == 0) {
            return;
        }

        String newFilename = assembleNewFilename(newFilenameWithoutSuffix, count, newFilenameSuffix);
        entity.setFilename(newFilename);

    }

    /**
     * 拼装新文件名称
     */
    private String assembleNewFilename(String newFilenameWithoutSuffix, int count, String newFilenameSuffix) {
        return newFilenameWithoutSuffix +
                FileConstants.CN_LEFT_PARENTHESES_STR +
                count +
                FileConstants.CN_RIGHT_PARENTHESES_STR +
                newFilenameSuffix;
    }

    /**
     * 查找文件夹下面的同名文件数量
     */
    private int getDuplicateFilename(RPanUserFile entity, String newFilenameWithoutSuffix) {
        QueryWrapper<RPanUserFile> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parent_id", entity.getParentId());
        queryWrapper.eq("folder_flag", entity.getFolderFlag());
        queryWrapper.eq("user_id", entity.getUserId());
        queryWrapper.eq("del_flag", DelFlagEnum.NO.getCode());
        queryWrapper.likeLeft("filename", newFilenameWithoutSuffix);
        return count(queryWrapper);
    }
}




