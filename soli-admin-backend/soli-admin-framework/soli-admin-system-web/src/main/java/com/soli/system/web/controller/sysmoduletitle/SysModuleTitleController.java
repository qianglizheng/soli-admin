package com.soli.system.web.controller.sysmoduletitle;

import com.soli.common.api.vo.Result;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.sysmoduletitle.SysModuleTitleSaveRequest;
import com.soli.system.service.sysmoduletitle.SysModuleTitleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 字段标题中心控制器
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:15
 */
@Tag(name = "字段标题中心", description = "字段标题中心相关接口")
@RestController
@RequestMapping("/sys/module-title")
@RequiredArgsConstructor
public class SysModuleTitleController {

    private final SysModuleTitleService service;

    @Operation(summary = "查询模块树")
    @PreAuthorize("hasAuthority('sys:module-title:page')")
    @GetMapping("/tree")
    public Result<List<SysModuleTreeNodeDTO>> tree() {
        return Result.success(service.queryModuleTree());
    }

    @Operation(summary = "查询模块详情")
    @PreAuthorize("hasAuthority('sys:module-title:page')")
    @GetMapping("/{moduleId}")
    public Result<SysModuleDetailDTO> detail(@PathVariable Long moduleId) {
        return Result.success(service.queryDetail(moduleId));
    }

    @Operation(summary = "保存字段标题配置")
    @PreAuthorize("hasAuthority('sys:module-title:save')")
    @PutMapping
    public Result<Void> save(@Valid @RequestBody SysModuleTitleSaveRequest request) {
        service.save(request);
        return Result.success();
    }

}
