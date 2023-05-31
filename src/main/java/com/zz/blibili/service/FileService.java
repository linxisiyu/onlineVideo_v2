package com.zz.blibili.service;

import com.zz.blibili.entity.File;
import com.zz.blibili.mapper.FileMapper;
import com.zz.blibili.utils.FastDFSUtil;
import com.zz.blibili.utils.MD5Util;
import io.netty.util.internal.StringUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author ZhangZhe
 */
@Service
public class FileService {
    @Autowired
    private FileMapper fileDao;
    @Autowired
    private FastDFSUtil fastDFSUtil;

    /**
     * @param slice
     * @param fileMD5
     * @param sliceNo
     * @param totalSliceNo
     * @return
     * @throws Exception
     */
    public String uploadFileBySlices(MultipartFile slice, String fileMD5,
                                     Integer sliceNo, Integer totalSliceNo) throws Exception {
        File dbFileMD5 = fileDao.getFileByMD5(fileMD5);
        if (dbFileMD5 != null) {
            return dbFileMD5.getUrl();
        }
        String url = fastDFSUtil.uploadFileBySlices(slice, fileMD5, sliceNo, totalSliceNo);
        if (!StringUtil.isNullOrEmpty(url)) {
            dbFileMD5 = new File();
            dbFileMD5.setCreateTime(new Date());
            dbFileMD5.setMd5(fileMD5);
            dbFileMD5.setUrl(url);
            dbFileMD5.setType(fastDFSUtil.getFileType(slice));
            fileDao.addFile(dbFileMD5);
        }
        return url;
    }

    public String getFileMD5(MultipartFile file) throws Exception {
        return MD5Util.getFileMD5(file);
    }

}