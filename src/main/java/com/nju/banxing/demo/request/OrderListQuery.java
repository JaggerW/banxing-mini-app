package com.nju.banxing.demo.request;

import com.nju.banxing.demo.common.BasePaged;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 预约申请列表
 * @Date: 2020/12/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderListQuery extends BasePaged {
    private static final long serialVersionUID = -4896814636773097777L;

    // true, 未处理 or 进行中 or 已同意 or 未评价；
    // false，已处理 or 已结束 or 已拒绝 or 已评价；
    private Boolean processFlag = true;
}
