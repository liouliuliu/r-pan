package com.liuhf.pan.server.modules.file.mapper;

import com.liuhf.pan.server.modules.file.context.QueryFileListContext;
import com.liuhf.pan.server.modules.file.entity.RPanUserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuhf.pan.server.modules.file.vo.RPanUserFileVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
* @author 52300
* @description 针对表【r_pan_user_file(用户文件信息表)】的数据库操作Mapper
* @createDate 2023-12-07 22:33:53
* @Entity com.liuhf.pan.server.modules.file.entity.RPanUserFile
*/
public interface RPanUserFileMapper extends BaseMapper<RPanUserFile> {

    /**
     * 查询用户的文件列表
     */
    List<RPanUserFileVO> selectFileList(@Param("param") QueryFileListContext context);
}




