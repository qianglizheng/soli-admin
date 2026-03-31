package com.soli.system.service.sysmodule;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改模块请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 00:05
 */
@Getter
@Setter
public class SysModuleModifyRequest {

    @NotNull(message = "模块 ID 不能为空")
    private Long id;

    @JsonSetter(nulls = Nulls.SKIP)
    private Long parentId = 0L;

    @NotBlank(message = "模块编码不能为空")
    private String moduleCode;

    @NotBlank(message = "模块名称不能为空")
    private String moduleName;

    @NotBlank(message = "模块类型不能为空")
    private String moduleType;

    private String routePath;

    private String componentPath;

    private String icon;

    @JsonSetter(nulls = Nulls.SKIP)
    private Integer sort = 1;

    @JsonSetter(nulls = Nulls.SKIP)
    private String navVisible = "1";

    @JsonSetter(nulls = Nulls.SKIP)
    private String statefulFlag = "0";

    private String stateFieldCode;

    @JsonSetter(nulls = Nulls.SKIP)
    private String status = "0";

    private String note;

}
