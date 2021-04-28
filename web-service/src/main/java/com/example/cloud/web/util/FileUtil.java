package com.example.cloud.web.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * @author 言曌
 * @date 2021/3/30 5:34 下午
 */
public class FileUtil {

    /**
     * 文件上传
     *
     * @param file
     * @return
     * @throws Exception
     */
    public static String upload(MultipartFile file) throws Exception {
        //用户目录
        final StrBuilder uploadPath = new StrBuilder(System.getProperties().getProperty("user.home"));
        uploadPath.append("/sens/upload/" + cn.hutool.core.date.DateUtil.thisYear()).append("/").append(cn.hutool.core.date.DateUtil.thisMonth() + 1).append("/");
        final File mediaPath = new File(uploadPath.toString());
        if (!mediaPath.exists()) {
            if (!mediaPath.mkdirs()) {
                throw new Exception("文件上传失败，无法创建文件夹");
            }
        }
        // 原始文件名
        String originFileName = file.getOriginalFilename();
        // 后缀
        final String fileSuffix = originFileName.substring(file.getOriginalFilename().lastIndexOf('.') + 1);
        // 新文件名
        String nameWithOutSuffix = UUID.randomUUID().toString().replaceAll("-", "");

        //带后缀
        String newFileName = nameWithOutSuffix + "." + fileSuffix;

        // 判断文件名是否已存在
        File descFile = new File(mediaPath.getAbsoluteFile(), newFileName.toString());
        file.transferTo(descFile);

        // 文件原路径
        final StrBuilder fullPath = new StrBuilder(mediaPath.getAbsolutePath());
        fullPath.append("/");
        fullPath.append(newFileName);

        //映射路径
        final StrBuilder filePath = new StrBuilder("/upload/");
        filePath.append(cn.hutool.core.date.DateUtil.thisYear());
        filePath.append("/");
        filePath.append(DateUtil.thisMonth() + 1);
        filePath.append("/");
        filePath.append(nameWithOutSuffix + "." + fileSuffix);

        return filePath.toString();
    }
}
