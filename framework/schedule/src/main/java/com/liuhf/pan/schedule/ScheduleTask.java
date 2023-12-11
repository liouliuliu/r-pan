package com.liuhf.pan.schedule;

/**
 * @author: lhf
 * @date: 2023/12/11 21:50
 * @description 定时任务的任务接口
 */
public interface ScheduleTask extends Runnable {


    /**
     * 获取定时任务的名称
     */
    String getName();
}
