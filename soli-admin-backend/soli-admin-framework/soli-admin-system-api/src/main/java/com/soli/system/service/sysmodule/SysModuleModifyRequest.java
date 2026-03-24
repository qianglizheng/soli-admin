package com.soli.system.service.sysmodule;

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

    private Long parentId;

    @NotBlank(message = "模块编码不能为空")
    private String moduleCode;

    @NotBlank(message = "模块名称不能为空")
    private String moduleName;

    @NotBlank(message = "模块类型不能为空")
    private String moduleType;

    private String routePath;

    private String componentPath;

    private String icon;

    private Integer sort;

    private String navVisible;

    private String statefulFlag;

    private String stateFieldCode;

    private String status;

    private String note;

}
