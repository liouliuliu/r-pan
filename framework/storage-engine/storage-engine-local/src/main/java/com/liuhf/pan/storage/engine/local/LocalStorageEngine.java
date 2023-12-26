package com.liuhf.pan.storage.engine.local;

import com.liuhf.pan.core.utils.FileUtil;
import com.liuhf.pan.core.utils.FileUtils;
import com.liuhf.pan.storage.engine.config.LocalStorageEngineConfig;
import com.liuhf.pan.storage.engine.core.AbstractStorageEngine;
import com.liuhf.pan.storage.engine.core.context.DeleteFileContext;
import com.liuhf.pan.storage.engine.core.context.StoreFileChunkContext;
import com.liuhf.pan.storage.engine.core.context.StoreFileContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

/**
 * @author: lhf
 * @date: 2023/12/25 21:42
 * @description 本地文件存储引擎实现类
 */
@Component
public class LocalStorageEngine extends AbstractStorageEngine {

    @Autowired
    private LocalStorageEngineConfig config;

    /**
     * 执行保存物理文件的动作，执行下沉到子类去执行
     *
     */
    @Override
    protected void doStore(StoreFileContext context) throws IOException {
        String basePath = config.getRootFilePath();
        String realFilePath = FileUtil.generateStoreFileRealPath(basePath, context.getFilename());
        FileUtil.writeStream2File(context.getInputStream(),new File(realFilePath),context.getTotalSize());
        context.setRealPath(realFilePath);
    }

    /**
     * 执行删除物理文件的动作
     * 下沉到子类去实现
     *
     */
    @Override
    protected void doDelete(DeleteFileContext context) throws IOException {
        FileUtil.deleteFiles(context.getRealFilePathList());
    }

    /**
     * 保存文件分片
     * 下沉到子类去实现
     *
     */
    @Override
    protected void doStoreChunk(StoreFileChunkContext context) throws IOException {
        String basePath = config.getRootFileChunkPath();
        String realFilePath = FileUtils.generateStoreFileChunkRealPath(basePath, context.getIdentifier(), context.getChunkNumber());
        FileUtils.writeStream2File(context.getInputStream(), new File(realFilePath), context.getTotalSize());
        context.setRealPath(realFilePath);
    }
}
