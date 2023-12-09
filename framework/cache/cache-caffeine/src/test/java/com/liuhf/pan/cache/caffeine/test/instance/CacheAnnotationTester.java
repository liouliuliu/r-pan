package com.liuhf.pan.cache.caffeine.test.instance;

import com.liuhf.pan.cache.core.constants.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

/**
 * @author: lhf
 * @date: 2023/12/9 20:17
 * @description Cache注解测试实体
 */
@Component
@Slf4j
public class CacheAnnotationTester {

    /**
     * 
     * @param name
     * @return
     */
    @Cacheable(cacheNames = CacheConstants.R_PAN_CACHE_NAME,key = "#name",sync = true)
    public String testCacheable(String name){
        log.info("call com.liuhf.pan.cache.caffeine.test.instance.CacheAnnotationTester.testCacheable, param is {}",name);
        return new StringBuilder("hello ").append(name).toString();
    }
}
