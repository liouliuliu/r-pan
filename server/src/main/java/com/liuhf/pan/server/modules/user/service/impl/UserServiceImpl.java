package com.liuhf.pan.server.modules.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuhf.pan.cache.core.constants.CacheConstants;
import com.liuhf.pan.core.exception.RPanBusinessException;
import com.liuhf.pan.core.response.ResponseCode;
import com.liuhf.pan.core.utils.IdUtil;
import com.liuhf.pan.core.utils.JwtUtil;
import com.liuhf.pan.core.utils.PasswordUtil;
import com.liuhf.pan.server.modules.file.constants.FileConstants;
import com.liuhf.pan.server.modules.file.context.CreateFolderContext;
import com.liuhf.pan.server.modules.file.service.IUserFileService;
import com.liuhf.pan.server.modules.user.constants.UserConstants;
import com.liuhf.pan.server.modules.user.context.UserLoginContext;
import com.liuhf.pan.server.modules.user.context.UserRegisterContext;
import com.liuhf.pan.server.modules.user.converter.UserConverter;
import com.liuhf.pan.server.modules.user.entity.RPanUser;
import com.liuhf.pan.server.modules.user.service.IUserService;
import com.liuhf.pan.server.modules.user.mapper.RPanUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Objects;

/**
 * @author 52300
 * @description 针对表【r_pan_user(用户信息表)】的数据库操作Service实现
 * @createDate 2023-12-07 22:31:49
 */
@Service(value = "userService")
public class UserServiceImpl extends ServiceImpl<RPanUserMapper, RPanUser> implements IUserService {


    @Resource
    private UserConverter userConverter;

    @Resource
    private IUserFileService iUserFileService;

    @Resource
    private CacheManager cacheManager;

    /**
     * 用户注册的业务实现
     */
    @Override
    public Long register(UserRegisterContext userRegisterContext) {
        assembleUserEntity(userRegisterContext);
        doRegister(userRegisterContext);
        createUserRootFolder(userRegisterContext);
        return userRegisterContext.getEntity().getUserId();
    }

    /**
     * 用户登录业务实现
     * <p>
     * 需要实现的功能：
     * 1、用户的登录信息校验
     * 2、生成一个具有时效性的accessToken
     * 3、将accessToken缓存起来，去实现单机登录
     */
    @Override
    public String login(UserLoginContext userLoginContext) {
        checkLoginInfo(userLoginContext);
        generateAndSaveAccessToken(userLoginContext);

        return userLoginContext.getAccessToken();
    }

    /**
     * 用户退出登录
     * 1.清除用户的登录凭证缓存
     */
    @Override
    public void exit(Long userId) {
        try {
            Cache cache = cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
            assert cache != null;
            cache.evict(UserConstants.USER_LOGIN_PREFIX + userId);
        }catch (Exception e){
            e.printStackTrace();
            throw new RPanBusinessException("用户退出登录失败！");
        }
    }

    //**************************************************private**************************************************

    /**
     * 创建用户的根目录信息
     */
    private void createUserRootFolder(UserRegisterContext userRegisterContext) {
        CreateFolderContext createUserFolderContext = new CreateFolderContext();
        createUserFolderContext.setParentId(FileConstants.TOP_PARENT_ID);
        createUserFolderContext.setUserId(userRegisterContext.getEntity().getUserId());
        createUserFolderContext.setFolderName(FileConstants.ALL_FILE_CN_STR);
        iUserFileService.createFolder(createUserFolderContext);
    }

    /**
     * 实现注册用户的业务
     * 需要捕获数据库的唯一索引冲突异常，来实现全局用户名称唯一
     */
    private void doRegister(UserRegisterContext userRegisterContext) {
        RPanUser entity = userRegisterContext.getEntity();
        if (Objects.nonNull(entity)) {

            try {
                if (!save(entity)) {
                    throw new RPanBusinessException("用户注册失败！");
                }
            } catch (DuplicateKeyException duplicateKeyException) {
                throw new RPanBusinessException("用户名已存在！");
            }
            return;
        }
        throw new RPanBusinessException(ResponseCode.ERROR);
    }

    /**
     * 实体转化，由上下文信息转化成用户实体，封装进上下文
     */
    private void assembleUserEntity(UserRegisterContext userRegisterContext) {
        RPanUser entity = userConverter.userRegisterContext2RPanUser(userRegisterContext);
        String salt = PasswordUtil.getSalt(),
                dbPassword = PasswordUtil.encryptPassword(salt, userRegisterContext.getPassword());
        entity.setUserId(IdUtil.get());
        entity.setSalt(salt);
        entity.setPassword(dbPassword);
        entity.setCreateTime(new Date());
        entity.setUpdateTime(new Date());
        userRegisterContext.setEntity(entity);
    }


    /**
     * 生成并保存登录之后的凭证
     */
    private void generateAndSaveAccessToken(UserLoginContext userLoginContext) {
        RPanUser entity = userLoginContext.getEntity();
        String token = JwtUtil.generateToken(entity.getUsername(), UserConstants.LOGIN_USER_ID, entity.getUserId(), UserConstants.ONE_DAY_LONG);
        Cache cache = cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
        assert cache != null;
        cache.put(UserConstants.USER_LOGIN_PREFIX + entity.getUserId(), token);
        userLoginContext.setAccessToken(token);
    }

    /**
     * 校验用户名密码
     */
    private void checkLoginInfo(UserLoginContext userLoginContext) {
        String username = userLoginContext.getUsername();
        String password = userLoginContext.getPassword();

        RPanUser entity = getRPanUserByUsername(username);
        if (Objects.isNull(entity)) {
            throw new RPanBusinessException("用户名称不存在");
        }

        String salt = entity.getSalt();
        String encPassword = PasswordUtil.encryptPassword(salt, password);
        String dbPassword = entity.getPassword();
        if (!Objects.equals(encPassword, dbPassword)) {
            throw new RPanBusinessException("密码信息不正确");
        }
        userLoginContext.setEntity(entity);
    }

    /**
     * 通过用户名称获取用户实体信息
     */
    private RPanUser getRPanUserByUsername(String username) {
        QueryWrapper<RPanUser> qw = new QueryWrapper<>();
        qw.eq("username", username);
        return getOne(qw);
    }
}




