package com.soli.system.service.sysmoduletitle;

import lombok.Getter;
import lombok.Setter;

/**
 * 模块字段标题配置对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysModuleFieldTitleDTO {

    /** 字段 ID */
    private Long fieldId;

    /** 显示标题 */
    private String displayTitle;

    /** 占位提示 */
    private String placeholder;

    /** 帮助说明 */
    private String helpText;

}
