package com.soli.system.service.sysdict;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典类型新增请求
 *
 * @author lizhengqiang
 * @since 2026-03-21 00:00
 */
@Getter
@Setter
public class SysDictCreateRequest {

    /** 字典名称 */
    @NotBlank(message = "字典名称不能为空")
    private String name;

    /** 字典类型 */
    @NotBlank(message = "字典类型不能为空")
    private String type;

    /** 字典状态 */
    @JsonSetter(nulls = Nulls.SKIP)
    private String status = "0";

    /** 备注 */
    private String note;

}
