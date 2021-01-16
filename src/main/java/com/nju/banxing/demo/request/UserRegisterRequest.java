package com.nju.banxing.demo.request;

import com.nju.banxing.demo.annotation.IsEmail;
import com.nju.banxing.demo.annotation.IsMobile;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 新用户注册请求对象类
 * @Date: 2020/11/5
 */
@Data
public class UserRegisterRequest implements Serializable {
    private static final long serialVersionUID = 3296637767087436552L;

    private String rawData;
    private String signature;
    private String encryptedData;
    private String iv;

    @NotNull(message = "昵称不能为空")
    @Length(min = 3, max = 8, message = "昵称长度需在3-8个字符之间")
    private String nickName;

    @NotNull(message = "手机号不能为空")
    @IsMobile
    private String mobile;

    @NotNull(message = "邮箱不能为空")
    @IsEmail
    private String email;

    // 手机号码是否改变
    private Boolean mobileFlag = true;

//    @NotNull(message = "请输入验证码")
    private String verCode;

}
