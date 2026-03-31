package com.soli.system.core.service.impl.sysmoduletitle;

import com.soli.common.api.enums.BinaryFlagEnum;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.core.entity.BaseEntity;
import com.soli.system.service.enums.ModuleComponentTypeEnum;
import com.soli.system.service.enums.ModuleValueTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * Module field title entity.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:58
 */
@Getter
@Setter
public class SysModuleFieldTitleEntity extends BaseEntity {

    private Long fieldId;

    private Long moduleId;

    private Long componentId;

    private String componentCode;

    private String fieldCode;

    private String defaultTitle;

    private String locale;

    private String displayTitle;

    private String placeholder;

    private String helpText;

    private ModuleComponentTypeEnum componentType;

    private String dataPath;

    private ModuleValueTypeEnum valueType;

    private BinaryFlagEnum requiredFlag;

    private Integer sort;

    private NormalDisableStatusEnum status;

}
