package com.nju.banxing.demo.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.aliyuncs.profile.DefaultProfile;
import com.nju.banxing.demo.config.AliyunConfig;
import com.nju.banxing.demo.config.AppContantConfig;
import com.nju.banxing.demo.vo.AliyunSmsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
