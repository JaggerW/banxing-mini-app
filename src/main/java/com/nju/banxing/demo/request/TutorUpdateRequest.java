package com.nju.banxing.demo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author: jaggerw
 * @Description: 导师信息更新
 * @Date: 2020/12/11
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TutorUpdateRequest extends BaseTutorInfo {
    private static final long serialVersionUID = 2603210599782970646L;

}
