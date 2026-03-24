package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块按钮实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:15
 */
@Getter
@Setter
public class SysModuleButtonEntity extends BaseEntity {

    private Long moduleId;

    private String buttonCode;

    private String defaultTitle;

    private String area;

    private Integer sort;

    private String status;

}
