package com.nju.banxing.demo.request;

import com.nju.banxing.demo.annotation.IsEmail;
import com.nju.banxing.demo.annotation.IsMobile;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

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

    @NotNull
    @Length(min = 3, max = 8)
    private String nickName;

    @NotNull
    @IsMobile
    private String mobile;

    @NotNull
    @IsEmail
    private String email;

    // 咨询方向List<Enum> 待定

}
