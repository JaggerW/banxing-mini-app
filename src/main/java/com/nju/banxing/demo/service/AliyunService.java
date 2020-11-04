package com.nju.banxing.demo.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nju.banxing.demo.config.AliyunConfig;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * @Author: jaggerw
 * @Description: 阿里云服务类
 * @Date: 2020/11/3
 */
@Service
@Slf4j
public class AliyunService {

    @Autowired
    private AliyunConfig aliyunConfig;

    /**
     * 发送短信
     * @param param
     * @return
     */
    public boolean sendSMS(AliyunSmsVO param){
        DefaultProfile defaultProfile = DefaultProfile.getProfile(
                AppContantConfig.ALIYUN_REGION_ID,aliyunConfig.getId(),aliyunConfig.getSecret());
        IAcsClient client = new DefaultAcsClient(defaultProfile);

        CommonRequest request = new CommonRequest();
        request.setSysMethod(MethodType.POST);
        request.setSysAction(AppContantConfig.ALIYUN_REQUEST_SENDSMS_ACTION);
        request.setSysDomain(AppContantConfig.ALIYUN_REQUEST_SENDSMS_DOMAIN);
        request.setSysVersion(AppContantConfig.ALIYUN_REQUEST_SENDSMS_VERSION);
        request.putQueryParameter("RegionId", AppContantConfig.ALIYUN_REGION_ID);
        request.putQueryParameter("PhoneNumbers", param.getPhoneNumber());
        request.putQueryParameter("SignName", param.getSignName());
        request.putQueryParameter("TemplateCode", param.getTemplateCode());
        request.putQueryParameter("OutId", param.getOutId());
        request.putQueryParameter("TemplateParam", param.getTemplateParam());

        try {
            CommonResponse response = client.getCommonResponse(request);
            log.info(response.getData());
            return true;
        } catch (ClientException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * 上传文件，返回可访问的url路径
     * @param folderName
     * @param fileName
     * @param inputStream
     * @return
     */
    public String uploadFile(String folderName, String fileName, InputStream inputStream){

        OSS ossClient = new OSSClientBuilder().build(
                AppContantConfig.ALIYUN_OSS_END_POINT, aliyunConfig.getId(), aliyunConfig.getSecret());
        log.info("Getting Started with OSS SDK for Java\n");
        try {
            if (!ossClient.doesBucketExist(AppContantConfig.ALIYUN_OSS_BUCKET)) {
                /*
                 * Create a new OSS bucket
                 */
                log.info("Creating bucket " + AppContantConfig.ALIYUN_OSS_BUCKET + "\n");
                ossClient.createBucket(AppContantConfig.ALIYUN_OSS_BUCKET);
                CreateBucketRequest createBucketRequest= new CreateBucketRequest(AppContantConfig.ALIYUN_OSS_BUCKET);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
                log.info("Creating bucket " + AppContantConfig.ALIYUN_OSS_BUCKET + "end\n");
            }
            log.info("Uploading a new object to OSS from a file\n");
            PutObjectResult putObjectResult = ossClient.putObject(new PutObjectRequest(AppContantConfig.ALIYUN_OSS_BUCKET, folderName + fileName, inputStream));
            if(putObjectResult != null){
                return AppContantConfig.ALIYUN_OSS_URL_PREFIX + folderName + fileName;
            }else{
                return null;
            }
        } catch (OSSException oe) {
            log.info("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            log.info("Error Message: " + oe.getErrorMessage());
            log.info("Error Code:       " + oe.getErrorCode());
            log.info("Request ID:      " + oe.getRequestId());
            log.info("Host ID:           " + oe.getHostId());
            return null;
        } finally {
            ossClient.shutdown();
        }

    }


}
