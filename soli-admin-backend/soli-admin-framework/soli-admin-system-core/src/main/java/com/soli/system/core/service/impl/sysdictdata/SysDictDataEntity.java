package com.soli.system.core.service.impl.sysdictdata;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.api.enums.YesNoEnum;
import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据实体
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:48
 */
@Getter
@Setter
public class SysDictDataEntity extends BaseEntity {

    /** 字典类型 ID */
    private Long dictTypeId;

    /** 字典标签 */
    private String label;

    /** 字典键值 */
    private String value;

    /** 显示排序 */
    private String sort;

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认 */
    private YesNoEnum defaultFlag;

    /** 状态 */
    private NormalDisableStatusEnum status;

}
