package com.soli.system.service.sysdictdata;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 字典数据修改请求
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:58
 */
@Getter
@Setter
public class SysDictDataModifyRequest {

    /** 字典数据 ID */
    @NotNull(message = "字典数据 ID 不能为空")
    private Long id;

    /** 字典类型 ID */
    @NotNull(message = "字典类型 ID 不能为空")
    private Long dictTypeId;

    /** 字典标签 */
    @NotBlank(message = "字典标签不能为空")
    private String label;

    /** 字典键值 */
    @NotBlank(message = "字典键值不能为空")
    private String value;

    /** 显示排序 */
    @JsonSetter(nulls = Nulls.SKIP)
    private String sort = "0";

    /** 样式属性 */
    private String cssClass;

    /** 表格回显样式 */
    private String listClass;

    /** 是否默认 */
    @JsonSetter(nulls = Nulls.SKIP)
    private String defaultFlag = "N";

    /** 状态 */
    @JsonSetter(nulls = Nulls.SKIP)
    private String status = "0";

    /** 备注 */
    private String note;

}
