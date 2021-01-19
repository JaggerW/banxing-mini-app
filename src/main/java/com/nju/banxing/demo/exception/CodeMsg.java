package com.nju.banxing.demo.exception;

import lombok.Data;
import org.aspectj.apache.bcel.classfile.Code;
import org.omg.CORBA.CODESET_INCOMPATIBLE;

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


    // 服务端 基础
    public static final CodeMsg SUCCESS = new CodeMsg(0,"success");
    public static final CodeMsg NULL_USER = new CodeMsg(-1,"对不起，您还没有注册");
    public static final CodeMsg NULL_TOKEN = new CodeMsg(-2,"token已失效，请重新登录");
    public static final CodeMsg SERVER_ERROR = new CodeMsg(100,"服务端异常");
    public static final CodeMsg BIND_ERROR = new CodeMsg(101,"%s");  // 参数校验异常

    // 服务端 用户
    public static final CodeMsg ERROR_VER_CODE = new CodeMsg(103,"验证失败，请重新获取验证码");
    public static final CodeMsg FAIL_REGISTER = new CodeMsg(104,"注册失败");
    public static final CodeMsg TUTOR_FAIL_REGISTER = new CodeMsg(105,"提交信息失败，请重新填写");
    public static final CodeMsg FAIL_UPLOAD = new CodeMsg(106,"附件上传失败");
    public static final CodeMsg NULL_TUTOR = new CodeMsg(107,"该导师不存在");
    public static final CodeMsg FAIL_UPDATE = new CodeMsg(108,"提交失败，请重新进行更改");
    public static final CodeMsg DUP_USER = new CodeMsg(109,"用户已存在，不可重复注册");
    public static final CodeMsg FAIL_UPDATE_TUTOR = new CodeMsg(110,"导师信息更新失败");
    public static final CodeMsg ERROR_MEETING_MESSAGE = new CodeMsg(111,"请粘贴正确的腾讯会议生成链接后再点击提交");
    public static final CodeMsg ERROR_MEETING_TIME = new CodeMsg(112,"会议开始时间与订单不符，请重新填写腾讯会议生成链接");
    public static final CodeMsg ERROR_WORK_TIME = new CodeMsg(113,"开始时间至少须早于结束时间十分钟");

    // 服务端 订单
    public static final CodeMsg ERROR_ORDER = new CodeMsg(500, "下单失败");
    public static final CodeMsg DUP_ORDER = new CodeMsg(501,"请勿重复下单");
    public static final CodeMsg ERROR_RESERVE_TIME = new CodeMsg(502,"预约时间错误");
    public static final CodeMsg ERROR_RESERVE_COST = new CodeMsg(503,"咨询费用错误");
    public static final CodeMsg OUT_OF_TIME_RANGE = new CodeMsg(504,"咨询时间不可超出导师当前可工作范围");
    public static final CodeMsg NULL_ORDER = new CodeMsg(505, "查无此单");

    // 服务端 资金
    public static final CodeMsg OCCUPY_ERROR = new CodeMsg(600, "待提现资金总额小于将转化为可提现资金");

    // 微信
    public static final CodeMsg WX_ERROR_CHECK_USER_INFO = new CodeMsg(200,"用户信息校验失败");
    public static final CodeMsg WX_ERROR_GET_USER_INFO = new CodeMsg(201,"用户信息获取失败");
    public static final CodeMsg FAIL_PAY_ERROR_ORDER = new CodeMsg(202,"微信支付失败：统一下单异常");
    public static final CodeMsg FAIL_REFUND_ERROR_ORDER = new CodeMsg(202,"申请微信退款失败");
    public static final CodeMsg FAIL_PAY_ERROR_SIGN = new CodeMsg(203,"微信支付回调失败:签名校验异常");
    public static final CodeMsg FAIL_PAY_ERROR_COM = new CodeMsg(204,"微信支付回调失败:通信异常");

    // 阿里云
    public static final CodeMsg SMS_ERROR = new CodeMsg(300,"短信发送失败");
    public static final CodeMsg ERROR_EXTENSION = new CodeMsg(301,"不支持该类型文件上传！");

    // 工具类
    public static final CodeMsg ERROR_DATE = new CodeMsg(400, "获取日期错误");

    // 其他
    public static final CodeMsg RETRY_ON_FAIL = new CodeMsg(700, "尝试失败，需要retry");
    public static final CodeMsg ERROR_PARSE_TIME = new CodeMsg(701,"时间解析错误");

    public CodeMsg fillArgs(Object ... args){
        int code  = this.code;
        String message = String.format(this.msg,args);
        return new CodeMsg(code,message);
    }
}
