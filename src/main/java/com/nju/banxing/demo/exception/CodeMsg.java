package com.nju.banxing.demo.exception;

import lombok.Data;
import org.aspectj.apache.bcel.classfile.Code;

/**
 * @Author: jaggerw
 * @Description: 错误码
 * @Date: 2020/11/2
 */
@Data
public class CodeMsg {
    private Integer code;
    private String msg;

    private CodeMsg(Integer code, String msg){
        this.code = code;
        this.msg = msg;
    }


    // 服务端
    public static CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static CodeMsg NULL_USER = new CodeMsg(-1,"对不起，您还没有注册");
    public static CodeMsg NULL_TOKEN = new CodeMsg(-2,"token已失效，请重新登录");
    public static CodeMsg SERVER_ERROR = new CodeMsg(100,"服务端异常");
    public static CodeMsg PARAM_ERROR = new CodeMsg(101,"参数校验异常：%s");
    public static CodeMsg OTHER_ERROR = new CodeMsg(102, "哎呀，不好意思出错了：%s");
    public static CodeMsg ERROR_VER_CODE = new CodeMsg(103,"验证失败，请重新获取验证码");

    // 微信
    public static CodeMsg WX_ERROR_CHECK_USER_INFO = new CodeMsg(200,"用户信息校验失败");
    public static CodeMsg WX_ERROR_GET_USER_INFO = new CodeMsg(200,"用户信息获取失败");

    // 阿里云
    public static CodeMsg SMS_ERROR = new CodeMsg(300,"短信发送失败");


    public CodeMsg fillArgs(Object ... args){
        int code  = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }
}
