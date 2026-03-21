package com.soli.system.service.sysconfig;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 参数配置新增请求
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:40
 */
@Getter
@Setter
public class SysConfigCreateRequest {

    /** 参数名称 */
    @NotBlank(message = "参数名称不能为空")
    private String configName;

    /** 参数键名 */
    @NotBlank(message = "参数键名不能为空")
    private String configKey;

    /** 参数键值 */
    private String configValue;

    /** 系统内置 */
    private String configType;

    /** 备注 */
    private String note;

}
