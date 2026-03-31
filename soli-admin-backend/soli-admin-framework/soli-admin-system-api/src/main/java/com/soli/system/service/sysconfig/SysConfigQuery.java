package com.soli.system.service.sysconfig;

import com.soli.common.api.enums.YesNoEnum;
import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 参数配置分页查询参数
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:40
 */
@Getter
@Setter
public class SysConfigQuery extends PageQuery {

    /** 参数名称 */
    private String configName;

    /** 参数键名 */
    private String configKey;

    /** 系统内置 */
    private YesNoEnum configType;

}
