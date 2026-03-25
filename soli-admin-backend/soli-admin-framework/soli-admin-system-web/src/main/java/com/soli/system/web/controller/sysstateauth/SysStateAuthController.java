package com.soli.system.web.controller.sysstateauth;

import com.soli.common.api.vo.Result;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.sysstateauth.SysStateAuthPageDetailDTO;
import com.soli.system.service.sysstateauth.SysStateAuthSaveRequest;
import com.soli.system.service.sysstateauth.SysStateAuthService;
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
 * 状态权限控制器
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:15
 */
@Tag(name = "状态权限中心", description = "状态权限中心相关接口")
@RestController
@RequestMapping("/sys/state-auth")
@RequiredArgsConstructor
public class SysStateAuthController {

    private final SysStateAuthService service;

    @Operation(summary = "查询状态型模块树")
    @PreAuthorize("hasAuthority('sys:state-auth:page')")
    @GetMapping("/tree")
    public Result<List<SysModuleTreeNodeDTO>> tree() {
        return Result.success(service.queryModuleTree());
    }

    @Operation(summary = "查询页面详情")
    @PreAuthorize("hasAuthority('sys:state-auth:page')")
    @GetMapping("/{moduleId}")
    public Result<SysStateAuthPageDetailDTO> detail(@PathVariable Long moduleId) {
        return Result.success(service.queryPageDetail(moduleId));
    }

    @Operation(summary = "保存状态限制")
    @PreAuthorize("hasAuthority('sys:state-auth:save')")
    @PutMapping
    public Result<Void> save(@Valid @RequestBody SysStateAuthSaveRequest request) {
        service.save(request);
        return Result.success();
    }

}
