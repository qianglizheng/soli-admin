package com.soli.system.service.sysmoduletitle;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 模块字段标题保存请求
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:05
 */
@Getter
@Setter
public class SysModuleTitleSaveRequest {

    @NotNull(message = "模块 ID 不能为空")
    private Long moduleId;

    private List<SysModuleFieldTitleDTO> fields;

}
