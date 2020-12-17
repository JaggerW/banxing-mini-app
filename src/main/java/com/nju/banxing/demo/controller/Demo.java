package com.nju.banxing.demo.controller;

import com.nju.banxing.demo.util.WxMessageUtil;
import com.nju.banxing.demo.vo.WxMessageVO;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: jaggerw
 * @Description: test
 * @Date: 2020/11/4
 */
@Slf4j
public class Demo{
    public static void main(String[] args) {
        String str = "JaggerW 邀请您参加腾讯会议\n" +
                "会议主题：JaggerW预定的会议\n" +
                "会议时间：2020/12/17 16:30-17:00\n" +
                "\n" +
                "点击链接入会，或添加至会议列表：\n" +
                "https://meeting.tencent.com/s/iKuCezTSYfAt\n" +
                "\n" +
                "会议 ID：994 812 412\n" +
                "会议密码：11111\n" +
                "\n" +
                "手机一键拨号入会\n" +
                "+8675536550000,,994812412# (中国大陆)\n" +
                "+85230018898,,,2,994812412# (中国香港)\n" +
                "\n" +
                "根据您的位置拨号\n" +
                "+8675536550000 (中国大陆)\n" +
                "+85230018898 (中国香港)";

        WxMessageVO wxMessageVO = WxMessageUtil.parseMes(str);
        System.out.println(wxMessageVO.toString());

    }
}
