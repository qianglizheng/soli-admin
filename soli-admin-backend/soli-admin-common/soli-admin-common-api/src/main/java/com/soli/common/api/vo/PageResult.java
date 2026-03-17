package com.soli.common.api.vo;

import com.github.pagehelper.PageInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 分页数据
 *
 * @author lizhengqiang
 * @since 2026-03-17 20:33
*/
@Getter
@Setter
public class PageResult<T> {
    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private List<T> list;

    public static <T> PageResult<T> of(Integer pageNum, Integer pageSize, Long total, List<T> list) {
        PageResult<T> r = new PageResult<>();
        r.setPageNum(pageNum);
        r.setPageSize(pageSize);
        r.setTotal(total);
        r.setList(list);
        return r;
    }

}