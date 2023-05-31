package com.zz.blibili.respository;


import com.zz.blibili.entity.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserInfoRepository extends ElasticsearchRepository<Video, Long> {

}
