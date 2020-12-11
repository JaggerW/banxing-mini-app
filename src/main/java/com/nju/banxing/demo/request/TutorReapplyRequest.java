package com.nju.banxing.demo.request;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @Author: jaggerw
 * @Description: 重新提交审核材料
 * @Date: 2020/12/11
 */
@Data
public class TutorReapplyRequest implements Serializable {
    private static final long serialVersionUID = 4366475428054089595L;

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
