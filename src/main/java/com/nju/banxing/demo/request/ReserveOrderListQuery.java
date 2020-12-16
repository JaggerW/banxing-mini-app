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
public class ReserveOrderListQuery extends BasePaged {
    private static final long serialVersionUID = -4896814636773097777L;

    // true, 未处理；false，已处理
    private Boolean processFlag;
}
