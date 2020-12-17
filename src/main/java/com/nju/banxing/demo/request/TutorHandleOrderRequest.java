package com.nju.banxing.demo.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 导师处理预约请求
 * @Date: 2020/12/17
 */
@Data
public class TutorHandleOrderRequest implements Serializable {
    private static final long serialVersionUID = 2889417545405732076L;

    @NotNull(message = "订单号不得为空")
    private String orderCode;
    // 状态同tutorStatusEnum：1，接受；2，拒绝
    private Integer handleType;
    private String content;

}
