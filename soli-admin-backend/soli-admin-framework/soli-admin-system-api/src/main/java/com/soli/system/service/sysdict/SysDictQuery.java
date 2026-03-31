package com.soli.system.service.sysdict;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典类型分页查询参数
 *
 * @author lizhengqiang
 * @since 2026-03-21 00:01
 */
@Getter
@Setter
public class SysDictQuery extends PageQuery {

    /** 字典名称 */
    private String name;

    /** 字典类型 */
    private String type;

    /** 字典状态 */
    private NormalDisableStatusEnum status;

}
