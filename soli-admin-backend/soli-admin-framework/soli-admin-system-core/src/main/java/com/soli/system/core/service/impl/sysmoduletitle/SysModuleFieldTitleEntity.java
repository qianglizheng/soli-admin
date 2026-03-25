package com.soli.system.core.service.impl.sysmoduletitle;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 模块字段标题实体
 *
 * @author lizhengqiang
 * @since 2026-03-25 22:10
 */
@Getter
@Setter
public class SysModuleFieldTitleEntity extends BaseEntity {

    private Long fieldId;

    private String locale;

    private String displayTitle;

    private String placeholder;

    private String helpText;

    private String status;

}
