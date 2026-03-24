package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块 Tab 实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:15
 */
@Getter
@Setter
public class SysModuleTabEntity extends BaseEntity {

    private Long moduleId;

    private String tabScope;

    private String tabCode;

    private String tabName;

    private Integer sort;

    private String status;

}
