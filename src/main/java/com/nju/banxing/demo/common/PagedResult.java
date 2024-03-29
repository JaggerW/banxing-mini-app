package com.nju.banxing.demo.common;

import com.nju.banxing.demo.exception.CodeMsg;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * @Author: jaggerw
 * @Description: 列表类型返回值
 * @Date: 2020/11/3
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PagedResult<T> extends BasePaged {
    private static final long serialVersionUID = 7512997405474131620L;

    private Integer code;
    private String msg;

    private List<T> data;

    private PagedResult(List<T> data, long index, long size, long total, long pages){
        this.code = 0;
        this.msg = "success";
        this.data = data;
        super.setPageIndex(index);
        super.setPageSize(size);
        super.setTotal(total);
        super.setOffset(index * data.size());
        super.setPages(pages);
    }

    private PagedResult(CodeMsg codeMsg){
        code = codeMsg.getCode();
        msg = codeMsg.getMsg();
        data = null;
    }

    public static <T> PagedResult<T> success(List<T> data, long index, long size){
        return new PagedResult<>(data, index, size, 0, 0);
    }

    public static <T> PagedResult<T> success(List<T> data, long index, long size, long total, long pages){
        return new PagedResult<>(data, index, size, total, pages);
    }

    public static <T> PagedResult<T> error(CodeMsg codeMsg){
        return new PagedResult<T>(codeMsg);
    }
}
