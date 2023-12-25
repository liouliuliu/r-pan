package com.liuhf.pan.server.modules.file.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuhf.pan.server.modules.file.entity.RPanFile;
import com.liuhf.pan.server.modules.file.service.IFileService;
import com.liuhf.pan.server.modules.file.mapper.RPanFileMapper;
import org.springframework.stereotype.Service;

/**
* @author 52300
* @description 针对表【r_pan_file(物理文件信息表)】的数据库操作Service实现
* @createDate 2023-12-07 22:33:53
*/
@Service
public class FileServiceImpl extends ServiceImpl<RPanFileMapper, RPanFile>
    implements IFileService {

}




