package com.liuhf.pan.schedule.test;

import com.liuhf.pan.schedule.ScheduleManager;
import com.liuhf.pan.schedule.test.config.ScheduleTestConfig;
import com.liuhf.pan.schedule.test.task.SimpleScheduleTask;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: lhf
 * @date: 2023/12/11 22:30
 * @description 定时任务模块的单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = ScheduleTestConfig.class)
public class ScheduleTaskTest {
    
    @Autowired
    private ScheduleManager manager;
    
    @Autowired
    private SimpleScheduleTask taskTask;
    
    @Test
    public void testRunScheduleTask() throws InterruptedException {
        String cron = "0/5 * * * * ? ";
        String key = manager.startTask(taskTask, cron);

        Thread.sleep(10000);
        
        cron =  "0/1 * * * * ? ";
        key = manager.changeTask(key,cron);

        Thread.sleep(10000);
        
        manager.stopTask(key);
    }
}
