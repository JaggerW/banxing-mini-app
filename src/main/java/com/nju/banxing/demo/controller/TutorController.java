package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.annotation.MethodLog;
import com.nju.banxing.demo.common.PagedResult;
import com.nju.banxing.demo.common.SingleResult;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.enums.ConsultationTypeEnum;
import com.nju.banxing.demo.exception.CodeMsg;
import com.nju.banxing.demo.exception.GlobalException;
import com.nju.banxing.demo.request.TutorRegisterRequest;
import com.nju.banxing.demo.service.AliyunService;
import com.nju.banxing.demo.service.TutorService;
import com.nju.banxing.demo.util.UUIDUtil;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import com.nju.banxing.demo.vo.TutorSimpleInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
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
    public SingleResult<Boolean> register(String openid,
                                          @Validated @RequestBody TutorRegisterRequest request){
        log.error("=======openid:{}",openid);
        Integer consultationType = request.getConsultationType();
        if(ConsultationTypeEnum.KAO_YAN.getCode().equals(consultationType)){
            checkParam(request);
        }

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
        if(!"PNG".equals(extension.toUpperCase()) && !"JPG".equals(extension.toUpperCase()) && !"JPEG".equals(extension.toUpperCase())){
            return SingleResult.error(CodeMsg.ERROR_EXTENSION);
        }
        String fileName = UUIDUtil.getImageFileName();
        String realName = fileName+extension;

        try {
            String url = aliyunService.uploadFile(AppContantConfig.ALIYUN_OSS_IMAGES_FOLDER, realName, file.getInputStream());
            return SingleResult.success(url);
        } catch (Exception e) {
            e.printStackTrace();
            return SingleResult.error(CodeMsg.FAIL_UPLOAD);
        }
    }

    private void checkParam(TutorRegisterRequest request){
        if(null == request.getFirstScore()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("初试成绩不能为空"));
        }
        if(null == request.getFirstRank() || null == request.getFirstTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("初试排名不能为空"));
        }
        if(request.getFirstRank() > request.getFirstTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("初试排名不能大于初试总人数"));
        }
        if(null == request.getSecondScore()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("复试成绩不能为空"));
        }
        if(null == request.getSecondRank() || null == request.getSecondTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("复试排名不能为空"));
        }
        if(request.getSecondRank() > request.getSecondTotal()){
            throw new GlobalException(CodeMsg.BIND_ERROR.fillArgs("复试排名不能大于复试总人数"));
        }
    }

}
