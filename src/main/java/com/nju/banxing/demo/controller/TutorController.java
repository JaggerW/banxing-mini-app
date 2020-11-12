package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.request.TutorRegisterRequest;
import com.nju.banxing.demo.service.AliyunService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @Author: jaggerw
 * @Description: 导师
 * @Date: 2020/11/12
 */
@RestController
@RequestMapping("/tutor")
@Slf4j
public class TutorController {


    @Autowired
    private TutorService tutorService;

    @Autowired
    private AliyunService aliyunService;

    @PostMapping("/register")
    @MethodLog("申请注册导师")
    public SingleResult<Boolean> register(String openid, @RequestBody TutorRegisterRequest request){
        boolean flag = tutorService.register(openid,request);
        if(flag){
            // todo 通知管理员审核


            return SingleResult.success(true);
        }else{
            return SingleResult.error(CodeMsg.TUTOR_FAIL_REGISTER);
        }
    }

    @PostMapping("/upload_image")
    @MethodLog("上传审核文件")
    public SingleResult<String> upload (@RequestBody MultipartFile file){

        if(ObjectUtils.isEmpty(file) || file.isEmpty()){
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "." + FilenameUtils.getExtension(originalFilename);
        String fileName = UUIDUtil.getFileName();
        String realName = fileName+extension;

        try {
            String url = aliyunService.uploadFile(AppContantConfig.ALIYUN_OSS_IMAGES_FOLDER, realName, file.getInputStream());
            return SingleResult.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }
    }

}
