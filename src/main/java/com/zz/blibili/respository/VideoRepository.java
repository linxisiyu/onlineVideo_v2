package com.zz.blibili.respository;

import com.zz.blibili.entity.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * @author ZhangZhe
 */
public interface VideoRepository extends ElasticsearchRepository<Video, Long> {


    //find by title like, spring data将其进行拆解，进行模糊查询
    Video findByTitleLike(String keyword);

}
