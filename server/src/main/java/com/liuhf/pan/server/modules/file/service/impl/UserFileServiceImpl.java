package com.liuhf.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuhf.pan.core.constants.RPanConstants;
import com.liuhf.pan.core.exception.RPanBusinessException;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.modules.file.constants.FileConstants;
import com.liuhf.pan.server.modules.file.context.CreateFolderContext;
import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import com.liuhf.pan.server.modules.file.enums.DelFlagEnum;
import com.liuhf.pan.server.modules.file.enums.FolderFlagEnum;
import com.liuhf.pan.server.modules.file.service.IUserFileService;
import com.liuhf.pan.server.modules.file.mapper.RPanUserFileMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @author 52300
 * @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Service实现
 * @createDate 2023-12-07 22:33:53
 */
@Service(value = "userFileService")
public class UserFileServiceImpl extends ServiceImpl<RPanUserFileMapper, RPanUserFile> implements IUserFileService {


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


    //**************************************************private**************************************************

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




