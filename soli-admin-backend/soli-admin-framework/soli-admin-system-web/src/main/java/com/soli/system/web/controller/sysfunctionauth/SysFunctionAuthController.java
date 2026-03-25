package com.soli.system.web.controller.sysfunctionauth;

import com.soli.common.api.vo.Result;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthPageDetailDTO;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthSaveRequest;
import com.soli.system.service.sysfunctionauth.SysFunctionAuthService;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import com.soli.system.service.sysorgpost.SysOrgPostTreeNodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 功能授权控制器
 *
 * @author lizhengqiang
 * @since 2026-03-25 11:15
 */
@Tag(name = "功能授权", description = "功能授权相关接口")
@RestController
@RequestMapping("/sys/function-auth")
@RequiredArgsConstructor
public class SysFunctionAuthController {

    private final SysFunctionAuthService service;

    @Operation(summary = "查询岗位树")
    @PreAuthorize("hasAuthority('sys:function-auth:page')")
    @GetMapping("/post-tree")
    public Result<List<SysOrgPostTreeNodeDTO>> postTree() {
        return Result.success(service.queryOrgPostTree());
    }

    @Operation(summary = "查询模块树")
    @PreAuthorize("hasAuthority('sys:function-auth:page')")
    @GetMapping("/module-tree")
    public Result<List<SysModuleTreeNodeDTO>> moduleTree() {
        return Result.success(service.queryModuleTree());
    }

    @Operation(summary = "查询页面详情")
    @PreAuthorize("hasAuthority('sys:function-auth:page')")
    @GetMapping("/detail")
    public Result<SysFunctionAuthPageDetailDTO> detail(@RequestParam Long orgPostId, @RequestParam Long moduleId) {
        return Result.success(service.queryPageDetail(orgPostId, moduleId));
    }

    @Operation(summary = "保存岗位基线权限")
    @PreAuthorize("hasAuthority('sys:function-auth:save')")
    @PutMapping
    public Result<Void> save(@Valid @RequestBody SysFunctionAuthSaveRequest request) {
        service.save(request);
        return Result.success();
    }

}
