package com.zz.blibili.mapper;

import com.zz.blibili.entity.File;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface FileMapper {
    //添加一个新文件
    Integer addFile(File file);
    //得到文件的md5加密
    File getFileByMD5(String md5);
}
