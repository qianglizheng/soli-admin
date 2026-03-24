package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块字段实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:15
 */
@Getter
@Setter
public class SysModuleFieldEntity extends BaseEntity {

    private Long moduleId;

    private Long tabId;

    private String fieldScope;

    private String fieldCode;

    private String defaultTitle;

    private String componentType;

    private String dataPath;

    private String valueType;

    private String requiredFlag;

    private Integer sort;

    private String status;

}
