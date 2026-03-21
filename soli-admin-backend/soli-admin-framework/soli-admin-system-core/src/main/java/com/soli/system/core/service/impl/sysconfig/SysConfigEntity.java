package com.soli.system.core.service.impl.sysconfig;

import com.soli.common.core.entity.BaseEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 参数配置实体
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:42
 */
@Getter
@Setter
public class SysConfigEntity extends BaseEntity {

    /** 参数名称 */
    private String configName;

    /** 参数键名 */
    private String configKey;

    /** 参数键值 */
    private String configValue;

    /** 系统内置 */
    private String configType;

}
