package com.soli.system.service.sysdict;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典类型 DTO
 *
 * @author lizhengqiang
 * @since 2026-03-21 00:00
 */
@Getter
@Setter
public class SysDictDTO extends BaseDTO {

    /** 字典名称 */
    private String name;

    /** 字典类型 */
    private String type;

    /** 字典状态 */
    private String status;

}
