package com.soli.system.service.sysconfig;

import com.soli.common.api.dto.BaseDTO;
import com.soli.common.api.enums.YesNoEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 参数配置 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:40
 */
@Getter
@Setter
public class SysConfigDTO extends BaseDTO {

    /** 参数名称 */
    private String configName;

    /** 参数键名 */
    private String configKey;

    /** 参数键值 */
    private String configValue;

    /** 系统内置 */
    private YesNoEnum configType;

}
