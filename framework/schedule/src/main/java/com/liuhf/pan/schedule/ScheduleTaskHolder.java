package com.liuhf.pan.schedule;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.concurrent.ScheduledFuture;

/**
 * @author: lhf
 * @date: 2023/12/11 22:18
 * @description 定时任务和任务结果的缓存对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleTaskHolder implements Serializable {

    /**
     * 执行任务的实体
     */
    private ScheduleTask scheduleTask;

    /**
     * 执行任务的结果实体
     */
    private ScheduledFuture scheduledFuture;
}
