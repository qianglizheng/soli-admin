package com.soli.system.core.service.impl.sysdict;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典类型实体
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:56
 */
@Getter
@Setter
public class SysDictEntity extends BaseEntity {

    /** 字典名称 */
    private String name;

    /** 字典类型 */
    private String type;

    /** 字典状态 */
    private NormalDisableStatusEnum status;

}
