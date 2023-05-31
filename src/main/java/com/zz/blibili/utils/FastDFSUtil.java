package com.zz.blibili.utils;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zz.blibili.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * @author ZhangZhe
 */
@Component
public class FastDFSUtil {
    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public static final String DEFAULT_GROUP = "group1";

    public static final int SLICE_SIZE = 1024 * 1024;


    /*获取文件类型*/
    public String getFileType(MultipartFile file) {
        if (file == null) throw new ConditionException("非法文件！");
        String filename = file.getOriginalFilename();
        assert filename != null;
        int index = filename.lastIndexOf(".");
        String fileType = null;
        try {
            fileType = filename.substring(index + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileType;
    }

    /*上传一般文件*/
    public String uploadCommonFile(MultipartFile file) throws Exception {
        Set<MetaData> metaDataSet = new HashSet<>();
        String fileType = this.getFileType(file);
        StorePath storePath = fastFileStorageClient.uploadFile(
                file.getInputStream(), file.getSize(), fileType, metaDataSet);
        return storePath.getPath();
    }

    /*删除*/
    public void deleteFile(String filePath){
        fastFileStorageClient.deleteFile(filePath);
    }


    /*返回可以断点续传的文件的路径*/
    public String uploadAppenderFile(MultipartFile file) throws Exception{
        String fileName = file.getOriginalFilename();
        String fileType = this.getFileType(file);
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP,
                file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    /*修改断点续传文件*/
    public void modifyAppenderFile(MultipartFile file, String filePath, long offset) throws Exception{
        appendFileStorageClient.modifyFile(DEFAULT_GROUP, filePath,
                file.getInputStream(), file.getSize(), offset);
    }

    public static final String PATH_KEY = "path-key:";
    public static final String UPLOAD_SIZE_KEY = "upload_size_key:";
    public static final String UPLOAD_NO_KEY = "upload_no_key:";

    /**
     * 断点续传
     * @param file
     * @param fileMd5
     * @param sliceNo
     * @param totalSliceNo
     * @return
     * @throws Exception
     */
    public String uploadFileBySlices(MultipartFile file, String fileMd5, Integer sliceNo,
                                     Integer totalSliceNo) throws Exception{
        if (file == null || sliceNo == null || totalSliceNo == null)
            throw new ConditionException("参数异常！");
        String pathKey = PATH_KEY + fileMd5;                //存入redis的路径key
        String uploadSizeKey = UPLOAD_SIZE_KEY + fileMd5;   //上传文件大小key
        String uploadNoKey = UPLOAD_NO_KEY + fileMd5;       //上传序号key
        //先从redis中拿出上传文件的大小
        String uploadSizeStr = redisTemplate.opsForValue().get(uploadSizeKey);
        Long uploadSize = 0L;
        if (!StringUtil.isNullOrEmpty(uploadSizeStr))
            uploadSize = Long.valueOf(uploadSizeStr);
        String fileType = this.getFileType(file);       //上传的文件类型

        //上传的是第一个分片，将文件路径以及分片序号写入redis
        //不是的话进行更新操作，更新这个文件的大小，以及将分片序号进行增加
        if (sliceNo == 1){
            String path = this.uploadAppenderFile(file);    //文件路径
            if (StringUtil.isNullOrEmpty(path)) throw new ConditionException("上传失败！");
            redisTemplate.opsForValue().set(pathKey, path);
            redisTemplate.opsForValue().set(uploadNoKey, "1");
        }else {
            String filePath = redisTemplate.opsForValue().get(pathKey);
            if (StringUtil.isNullOrEmpty(filePath)) throw new ConditionException("上传失败！");
            this.modifyAppenderFile(file, filePath, uploadSize);
            redisTemplate.opsForValue().increment(uploadNoKey);
        }
        //修改历史上传分片文件大小
        uploadSize += file.getSize();
        redisTemplate.opsForValue().set(uploadSizeKey, String.valueOf(uploadSize));
        //如果所有分片全部上传完毕，清空redis中对应的key和value
        String uploadNoStr = redisTemplate.opsForValue().get(uploadNoKey);
        Integer uploadNo = Integer.valueOf(uploadNoStr);
        String resultPath = "";
        if (uploadNo.equals(totalSliceNo)){
            resultPath = redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(uploadNoKey, pathKey, uploadSizeKey);
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }

    /*分片方法*/
    public void convertFileToSlices(MultipartFile multipartFile) throws Exception{
        String fileName = multipartFile.getOriginalFilename();  //得到源文件名
        String fileType = this.getFileType(multipartFile);      //得到文件类型
        File file = this.multipartFileToFile(multipartFile);    //转化为普通file类型
        long fileLength = file.length();
        int count = 1;
        for (int i = 0; i < fileLength; i += SLICE_SIZE){
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);       //每次从分片后面追加
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = "/output/" + fileName +count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        file.delete();
    }

    //multipartFile转换为普通file类型。
    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFilename = multipartFile.getOriginalFilename();
        String[] fileName = originalFilename.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    @Value("${fdfs.http.storage-addr}")
    private String httpFdfsStorageAddr;

    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String path) throws Exception {
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long totalFileSize = fileInfo.getFileSize();
        String url = httpFdfsStorageAddr + path;
        Enumeration<String> headerNames = request.getHeaderNames();
        HashMap<String, Object> headers = new HashMap<>();
        while (headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header, request.getHeader(header));
        }
        String rangeStr = request.getHeader("Range");
        String[] range;
        if (StringUtil.isNullOrEmpty(rangeStr)){
            rangeStr = "bytes=0-" + (totalFileSize - 1);
        }
        range = rangeStr.split("bytes=|-");
        long begin = 0;
        if (range.length >= 2){
            begin = Long.parseLong(range[1]);
        }
        long end = totalFileSize - 1;
        if (range.length >= 3){
            end = Long.parseLong(range[2]);
        }
        long len = end - begin + 1;
        String contentRange = "bytes " + begin + "-" + "/" + totalFileSize;
        response.setHeader("Content-Range", contentRange);
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Type", "video/mp4");
        response.setContentLength((int)len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        HttpUtil.get(url, headers, response);
    }
}
