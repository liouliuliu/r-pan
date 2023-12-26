package com.liuhf.pan.storage.engine.core;

import com.liuhf.pan.storage.engine.core.context.DeleteFileContext;
import com.liuhf.pan.storage.engine.core.context.StoreFileChunkContext;
import com.liuhf.pan.storage.engine.core.context.StoreFileContext;

import java.io.IOException;

/**
 * @author: lhf
 * @date: 2023/12/25 21:35
 * @description 文件存储引擎的顶级接口
 */
public interface StorageEngine {

    /**
     * 存储物理文件
     */
    void store(StoreFileContext context) throws IOException;

    /**
     * 删除物理文件
     */
    void delete(DeleteFileContext context) throws IOException;

    /**
     * 存储物理文件的分片
     */
    void storeChunk(StoreFileChunkContext context) throws IOException;
}
