package com.lee.controller;

import com.lee.VO.HttpJSONResult;
import com.lee.VO.PagedResult;
import com.lee.enums.VideoStatusEnum;
import com.lee.pojo.Bgm;
import com.lee.service.VideoService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@Controller
@RequestMapping("video")
public class VideoController {

    @Autowired
    private VideoService videoService;

    /**
     * 显示添加bgm的页面
     */
    @GetMapping("/showAddBgm")
    public String login() {
        return "video/addBgm";
    }

    /**
     * 跳转举报列表页
     */
    @GetMapping("/showReportList")
    public String showReportList() {
        return "video/reportList";
    }

    /**
     * 跳转bgm列表页
     */
    @GetMapping("/showBgmList")
    public String showBgmList() {
        return "video/bgmList";
    }


    /**
     * 上传bgm
     */
    @PostMapping("/bgmUpload")
    @ResponseBody
    public HttpJSONResult bgmUpload(@RequestParam("file") MultipartFile[] files) throws Exception {

        // 文件保存的命名空间
//		String fileSpace = File.separator + "imooc_videos_dev" + File.separator + "mvc-bgm";
        //File.separator会根据系统环境动态替换分隔符
        String fileSpace = "D:" + File.separator + "微信小程序" + File.separator + "weixin_videos_admin" + File.separator + "mvc-bgm";
        // 保存到数据库中的相对路径
        String uploadPathDB = File.separator + "bgm";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalPath = fileSpace + uploadPathDB + File.separator + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += (File.separator + fileName);

                    File outFile = new File(finalPath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return HttpJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return HttpJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }
        return HttpJSONResult.ok(uploadPathDB);
    }


    /**
     * 添加bgm
     */
    @PostMapping("/addBgm")
    @ResponseBody
    public HttpJSONResult addBgm(Bgm bgm) {

        videoService.addBgm(bgm);
        return HttpJSONResult.ok();
    }


    /**
     * 分页展示bgm列表
     */
    @PostMapping("/queryBgmList")
    @ResponseBody
    public PagedResult queryBgmList(Integer page) {
        return videoService.queryBgmList(page, 10);
    }

    /**
     * 删除bgm
     */
    @PostMapping("/delBgm")
    @ResponseBody
    public HttpJSONResult delBgm(String bgmId) {
        videoService.deleteBgm(bgmId);
        return HttpJSONResult.ok();
    }


    /**
     * 展示举报列表页
     */
    @PostMapping("/reportList")
    @ResponseBody
    public PagedResult reportList(Integer page) {
        PagedResult result = videoService.queryReportList(page, 10);
        return result;
    }

    /**
     * 禁播视频(更新视频状态即可)
     */
    @PostMapping("/forbidVideo")
    @ResponseBody
    public HttpJSONResult forbidVideo(String videoId) {
        videoService.updateVideoStatus(videoId, VideoStatusEnum.FORBID.value);
        return HttpJSONResult.ok();
    }

}
