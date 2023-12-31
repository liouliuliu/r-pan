package com.liuhf.pan.storage.engine.fastdfs;

import com.liuhf.pan.storage.engine.core.AbstractStorageEngine;
import com.liuhf.pan.storage.engine.core.context.DeleteFileContext;
import com.liuhf.pan.storage.engine.core.context.MergeFileContext;
import com.liuhf.pan.storage.engine.core.context.StoreFileChunkContext;
import com.liuhf.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author: lhf
 * @date: 2023/12/25 21:46
 * @description
 */
@Component
public class FastDFSStorageEngine extends AbstractStorageEngine {

    /**
     * 执行保存物理文件的动作，执行下沉到子类去执行
     *
     * @param context
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        
    }

    /**
     * 执行删除物理文件的动作
     * 下沉到子类去实现
     *
     * @param context
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {

    }

    /**
     * 保存文件分片
     * 下沉到子类去实现
     *
     * @param context
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException{
        
    }

    /**
     * 执行文件分片的动作
     * 下沉到子类实现
     *
     * @param context
     */
    @Override
    protected void doMergeFile(MergeFileContext context) throws IOException {
        
    }
}
