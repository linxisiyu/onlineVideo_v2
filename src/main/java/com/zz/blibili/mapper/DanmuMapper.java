package com.zz.blibili.mapper;

import com.zz.blibili.entity.Danmu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface DanmuMapper {

    /*添加弹幕*/
    void addDanmu(Danmu danmu);

    List<Danmu> getDanmus(@Param("params") Map<String, Object> params);
}
