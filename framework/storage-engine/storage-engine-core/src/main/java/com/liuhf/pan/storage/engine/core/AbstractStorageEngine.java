package com.liuhf.pan.storage.engine.core;

import com.liuhf.pan.cache.core.constants.CacheConstants;
import com.liuhf.pan.core.exception.RPanFrameworkException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Objects;

/**
 * @author: lhf
 * @date: 2023/12/25 21:36
 * @description 顶级文件存储引擎的公用父类
 */
public abstract class AbstractStorageEngine implements StorageEngine {

    @Autowired
    private CacheManager cacheManager;

    protected Cache getCache() {
        if (Objects.isNull(cacheManager)) {
            throw new RPanFrameworkException("this cache manager is empty!");
        }
        return cacheManager.getCache(CacheConstants.R_PAN_CACHE_NAME);
    }
    

}
