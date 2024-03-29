package com.nju.banxing.demo.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * @Author: jaggerw
 * @Description: 导师注册申请
 * @Date: 2020/11/12
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class TutorRegisterRequest extends BaseTutorInfo {
    private static final long serialVersionUID = -1289429884147287091L;

    @NotNull(message = "当前学校不能为空")
    private String currentUniversity;

    @NotNull(message = "当前专业不能为空")
    private String currentProfession;

    @NotNull(message = "审核证明材料不能为空")
    private String studentCardHome;

    @NotNull(message = "审核证明材料不能为空")
    private String studentCardInfo;

    @NotNull(message = "审核证明材料不能为空")
    private String studentCardRegister;

}
