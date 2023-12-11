package com.liuhf.pan.schedule;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * @author: lhf
 * @date: 2023/12/11 21:47
 * @description 定时模块配置类
 */
@SpringBootConfiguration
public class ScheduleConfig {
    
    @Bean
    public ThreadPoolTaskScheduler taskScheduler(){
        return new ThreadPoolTaskScheduler();
    }
}
