package com.liuhf.pan.server.modules.file;

import cn.hutool.core.lang.Assert;
import com.google.common.collect.Lists;
import com.liuhf.pan.core.exception.RPanBusinessException;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.server.RPanServerLauncher;
import com.liuhf.pan.server.modules.file.context.*;
import com.liuhf.pan.server.modules.file.entity.RPanFile;
import com.liuhf.pan.server.modules.file.enums.DelFlagEnum;
import com.liuhf.pan.server.modules.file.service.IFileService;
import com.liuhf.pan.server.modules.file.service.IUserFileService;
import com.liuhf.pan.server.modules.file.vo.RPanUserFileVO;
import com.liuhf.pan.server.modules.user.context.UserLoginContext;
import com.liuhf.pan.server.modules.user.context.UserRegisterContext;
import com.liuhf.pan.server.modules.user.service.IUserService;
import com.liuhf.pan.server.modules.user.vo.UserInfoVO;
import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @author: lhf
 * @date: 2023/12/20 21:53
 * @description 文件模块单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RPanServerLauncher.class)
@Transactional
public class FileTest {
    
    @Autowired
    private IUserFileService userFileService;

    @Autowired
    private IUserService userService;
    
    @Autowired
    private IFileService fileService;

    /**
     * 测试用户查询文件列表成功
     */
    @Test
    public void testQueryUserFileListSuccess(){
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        QueryFileListContext context = new QueryFileListContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFileTypeArray(null);
        context.setDelFlag(DelFlagEnum.NO.getCode());

        List<RPanUserFileVO> result = userFileService.getFileList(context);
        Assert.isTrue(CollectionUtils.isEmpty(result));
    }

    /**
     * 创建文件夹成功
     */
    @Test
    public void testCreateFolderSuccess(){
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);
        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

    }

    /**
     * 测试文件重命名失败-文件ID无效
     */
    @Test(expected = RPanBusinessException.class)
    public void testUpdateFilenameFailByWrongFileId() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId + 1);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("folder-name-new");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 测试当前用户ID无效
     */
    @Test(expected = RPanBusinessException.class)
    public void testUpdateFilenameFailByWrongUserId() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId + 1);
        updateFilenameContext.setNewFilename("folder-name-new");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 测试文件名称重复
     */
    @Test(expected = RPanBusinessException.class)
    public void testUpdateFilenameFailByWrongFilename() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("folder-name");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 校验文件名称已被占用
     */
    @Test(expected = RPanBusinessException.class)
    public void testUpdateFilenameFailByFilenameUnAvailable() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-1");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-2");

        fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("folder-name-1");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 测试更新文件名称成功
     */
    @Test
    public void testUpdateFilenameSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        UpdateFilenameContext updateFilenameContext = new UpdateFilenameContext();
        updateFilenameContext.setFileId(fileId);
        updateFilenameContext.setUserId(userId);
        updateFilenameContext.setNewFilename("folder-name-new");

        userFileService.updateFilename(updateFilenameContext);
    }

    /**
     * 校验文件删除失败-非法的文件ID
     */
    @Test(expected = RPanBusinessException.class)
    public void testDeleteFileFailByWrongFileId() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId + 1);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);

        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 校验文件删除失败-非法的用户ID
     */
    @Test(expected = RPanBusinessException.class)
    public void testDeleteFileFailByWrongUserId() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId + 1);

        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 校验用户删除文件成功
     */
    @Test
    public void testDeleteFileSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        CreateFolderContext context = new CreateFolderContext();
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);
        context.setFolderName("folder-name-old");

        Long fileId = userFileService.createFolder(context);
        Assert.notNull(fileId);

        DeleteFileContext deleteFileContext = new DeleteFileContext();
        List<Long> fileIdList = Lists.newArrayList();
        fileIdList.add(fileId);
        deleteFileContext.setFileIdList(fileIdList);
        deleteFileContext.setUserId(userId);

        userFileService.deleteFile(deleteFileContext);
    }

    /**
     * 校验秒传文件成功
     */
    @Test
    public void testSecUploadSuccess() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        String identifier = "123456789";

        RPanFile record = new RPanFile();
        record.setFileId(IdUtil.get());
        record.setFilename("filename");
        record.setRealPath("realpath");
        record.setFileSize("fileSize");
        record.setFileSizeDesc("fileSizeDesc");
        record.setFilePreviewContentType("");
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        fileService.save(record);

        SecUploadFileContext context = new SecUploadFileContext();
        context.setIdentifier(identifier);
        context.setFilename("filename");
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);

        boolean result = userFileService.secUpload(context);
        Assert.isTrue(result);
    }

    /**
     * 校验秒传文件失败
     */
    @Test
    public void testSecUploadFail() {
        Long userId = register();
        UserInfoVO userInfoVO = info(userId);

        String identifier = "123456789";

        RPanFile record = new RPanFile();
        record.setFileId(IdUtil.get());
        record.setFilename("filename");
        record.setRealPath("realpath");
        record.setFileSize("fileSize");
        record.setFileSizeDesc("fileSizeDesc");
        record.setFilePreviewContentType("");
        record.setIdentifier(identifier);
        record.setCreateUser(userId);
        record.setCreateTime(new Date());
        fileService.save(record);

        SecUploadFileContext context = new SecUploadFileContext();
        context.setIdentifier(identifier + "_update");
        context.setFilename("filename");
        context.setParentId(userInfoVO.getRootFileId());
        context.setUserId(userId);

        boolean result = userFileService.secUpload(context);
        Assert.isFalse(result);
    }


    //**************************************************private**************************************************

    /**
     * 生成模拟的网络文件实体
     *
     * @return
     */
    private static MultipartFile genarateMultipartFile() {
        MultipartFile file = null;
        try {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < 1024 * 1024; i++) {
                stringBuffer.append("a");
            }
            file = new MockMultipartFile("file", "test.txt", "multipart/form-data", stringBuffer.toString().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 用户注册
     *
     * @return 新用户的ID
     */
    private Long register() {
        UserRegisterContext context = createUserRegisterContext();
        Long register = userService.register(context);
        Assert.isTrue(register.longValue() > 0L);
        return register;
    }

    /**
     * 查询登录用户的基本信息
     *
     */
    private UserInfoVO info(Long userId) {
        UserInfoVO userInfoVO = userService.info(userId);
        Assert.notNull(userInfoVO);
        return userInfoVO;
    }

    private static final String USERNAME = "liuhf";
    private static final String PASSWORD = "12345678";
    private static final String QUESTION = "question";
    private static final String ANSWER = "question";

    private UserRegisterContext createUserRegisterContext() {
        UserRegisterContext context = new UserRegisterContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        context.setQuestion(QUESTION);
        context.setAnswer(ANSWER);
        return context;
    }

    private UserLoginContext createUserLoginContext() {
        UserLoginContext context = new UserLoginContext();
        context.setUsername(USERNAME);
        context.setPassword(PASSWORD);
        return context;
    }
}
