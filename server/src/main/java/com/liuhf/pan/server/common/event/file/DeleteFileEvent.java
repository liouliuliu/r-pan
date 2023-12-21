package com.liuhf.pan.server.common.event.file;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;

import java.util.List;

/**
 * @author: lhf
 * @date: 2023/12/21 22:05
 * @description 文件删除事件
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class DeleteFileEvent extends ApplicationEvent {
    private List<Long> fileIdList;
    
    public DeleteFileEvent(Object source,List<Long> fileIdList){
        super(source);
        this.fileIdList = fileIdList;
    }
}
