package com.soli.system.service.sysdictdata;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据分页查询参数
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:58
 */
@Getter
@Setter
public class SysDictDataQuery extends PageQuery {

    /** 字典类型 ID */
    private Long dictTypeId;

    /** 字典标签 */
    private String label;

    /** 字典键值 */
    private String value;

    /** 状态 */
    private NormalDisableStatusEnum status;

}
