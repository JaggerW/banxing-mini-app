package com.nju.banxing.demo.vo;

import com.nju.banxing.demo.request.TutorReapplyRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 审核信息
 * @Date: 2020/12/22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TutorApplyInfoVO extends TutorReapplyRequest {
    private static final long serialVersionUID = -5268438595906016062L;
}
