package com.nie.common.file;


import com.nie.common.tools.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/common")
@Slf4j
public class FileController {
    @Value("${basePath.path}")
    private String bpath;

    //文件图片的上传
    @PostMapping("/upload")
    public Result upload(MultipartFile file) {

        //还需要原文件名的后缀
        String originalFilename = file.getOriginalFilename();
        String substring = originalFilename.substring(originalFilename.lastIndexOf("."));
        //文件名使用随机文件名，避免重名造成覆盖
        String fname = UUID.randomUUID().toString() + substring;
        //还需要判断目录是否存在
        File dir = new File(bpath);
        if (!dir.exists())
            dir.mkdirs();
        try {
            //此时接收到的文件是个临时文件，需要转存到某个指定的位置
            file.transferTo(new File(bpath + fname));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.success(fname);
    }

    //文件的下载
    @GetMapping("download")
    public void downLoad(String name, HttpServletResponse response) {
        try {
            //通过输入流读取文件内容到一个byte数组
            FileInputStream fileInputStream = new FileInputStream(new File(bpath + name));

            //通过输出流将读到的文件写回到浏览器以展示图片
            response.setContentType("image/jpeg");
            ServletOutputStream outputStream = response.getOutputStream();
            int len = 0;
            byte[] bytes = new byte[1024];
            while ((len = fileInputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, len);
                outputStream.flush();
            }

            outputStream.close();
            fileInputStream.close();

        } catch (Exception e) {
            log.error("file download failed", e);
        }

    }

}
