package com.example.dangjian_spring.Controller;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.Dict;
import com.example.dangjian_spring.common.AuthAccess;
import com.example.dangjian_spring.common.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@RestController
@RequestMapping("/file")
public class FileController {

    private static final String ROOT_PATH = System.getProperty("user.dir") + File.separator + "files";
    private static final String DOWNLOAD_PREFIX = "/file/download/";

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        return Result.success(generateUrl(file));
    }

    @AuthAccess
    @GetMapping("/download/**")
    public void download(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        int idx = uri.indexOf(DOWNLOAD_PREFIX);
        if (idx < 0) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String fileName = URLDecoder.decode(uri.substring(idx + DOWNLOAD_PREFIX.length()), StandardCharsets.UTF_8);
        if (fileName.isEmpty() || fileName.contains("..")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        File file = new File(ROOT_PATH + File.separator + fileName);
        if (!file.isFile()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        String contentType = Files.probeContentType(file.toPath());
        if (contentType == null) {
            contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
        }
        response.setContentType(contentType);
        response.setContentLengthLong(file.length());
        Files.copy(file.toPath(), response.getOutputStream());
        response.flushBuffer();
    }

    @PostMapping("/editor/upload")
    public Dict editorUpload(MultipartFile file) throws IOException {
        String url = generateUrl(file);
        Dict dict = Dict.create().set("errno", 0).set("data", CollUtil.newArrayList(Dict.create().set("url", url)));
        return dict;
    }

    private String generateUrl(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "upload_" + System.currentTimeMillis();
        }
        originalFilename = FileUtil.getName(originalFilename);
        String mainName = FileUtil.mainName(originalFilename);
        String extName = FileUtil.extName(originalFilename);

        File parentFile = new File(ROOT_PATH);
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
        File saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        if (saveFile.exists()) {
            originalFilename = System.currentTimeMillis() + "_" + mainName;
            if (extName != null && !extName.isEmpty()) {
                originalFilename = originalFilename + "." + extName;
            }
            saveFile = new File(ROOT_PATH + File.separator + originalFilename);
        }
        file.transferTo(saveFile);
        return DOWNLOAD_PREFIX + originalFilename;
    }
}
