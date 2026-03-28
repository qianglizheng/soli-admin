package com.soli.system.core.service.impl.sysmodule;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * Module field entity.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:58
 */
@Getter
@Setter
public class SysModuleFieldEntity extends BaseEntity {

    private Long moduleId;

    private Long componentId;

    private String componentCode;

    private String fieldCode;

    private String defaultTitle;

    private String displayTitle;

    private String placeholder;

    private String helpText;

    private String componentType;

    private String dataPath;

    private String valueType;

    private String requiredFlag;

    private Integer sort;

    private String status;

}
