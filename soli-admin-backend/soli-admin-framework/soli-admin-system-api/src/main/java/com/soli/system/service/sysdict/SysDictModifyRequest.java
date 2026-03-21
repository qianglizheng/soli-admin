package com.soli.system.service.sysdict;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典类型修改请求
 *
 * @author lizhengqiang
 * @since 2026-03-21 00:01
 */
@Getter
@Setter
public class SysDictModifyRequest {

    /** 字典 ID */
    @NotNull(message = "字典 ID 不能为空")
    private Long id;

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    private String name;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    private String type;

    /** 字典状态 */
    private String status;

    /** 备注 */
    private String note;

}
