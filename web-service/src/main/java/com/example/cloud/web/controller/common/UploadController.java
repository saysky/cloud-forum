package com.example.cloud.web.controller.common;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.text.StrBuilder;
import com.example.cloud.web.util.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传
 *
 * @author 言曌
 * @date 2018/4/13 下午9:30
 */

@Controller
public class UploadController {


    @PostMapping("/upload")
    @ResponseBody
    public Map<String, String> uploadToQiNiu(@RequestParam(value = "upload_file", required = false) MultipartFile file) throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        try {
            String filePath = FileUtil.upload(file);
            map.put("success", "true");
            map.put("msg", "成功");
            map.put("file_path", filePath);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
