package com.soli.system.web.controller.sysconfig;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysconfig.SysConfigConverter;
import com.soli.system.service.sysconfig.SysConfigCreateRequest;
import com.soli.system.service.sysconfig.SysConfigDTO;
import com.soli.system.service.sysconfig.SysConfigModifyRequest;
import com.soli.system.service.sysconfig.SysConfigQuery;
import com.soli.system.service.sysconfig.SysConfigService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 参数配置控制器
 *
 * @author lizhengqiang
 * @since 2026-03-22 01:44
 */
@Tag(name = "参数配置", description = "参数配置相关接口")
@RestController
@RequestMapping("/sys/config")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class SysConfigController {

    private final SysConfigService sysConfigService;

    private final SysConfigConverter sysConfigConverter;

    @Operation(summary = "新增参数配置")
    @PreAuthorize("@moduleAccess.hasButton('sys_config', 'create')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysConfigCreateRequest createRequest) {
        sysConfigService.create(sysConfigConverter.toDTO(createRequest));
        return Result.success();
    }

    @Operation(summary = "删除参数配置")
    @PreAuthorize("@moduleAccess.hasButton('sys_config', 'remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysConfigService.remove(id);
        return Result.success();
    }

    @Operation(summary = "修改参数配置")
    @PreAuthorize("@moduleAccess.hasButton('sys_config', 'modify')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysConfigModifyRequest modifyRequest) {
        sysConfigService.modify(sysConfigConverter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "分页查询参数配置")
    @PreAuthorize("@moduleAccess.hasModule('sys_config')")
    @PostMapping("/page")
    public Result<PageResult<SysConfigDTO>> page(@RequestBody SysConfigQuery query) {
        return Result.success(sysConfigService.page(query));
    }

    @Operation(summary = "查询参数配置详情")
    @PreAuthorize("@moduleAccess.hasModule('sys_config')")
    @GetMapping("/{id}")
    public Result<SysConfigDTO> getById(@PathVariable Long id) {
        return Result.success(sysConfigService.getById(id).orElseThrow(() -> new BusinessException("指定参数不存在！")));
    }

    @Operation(summary = "刷新参数缓存")
    @PreAuthorize("@moduleAccess.hasButton('sys_config', 'refreshCache')")
    @DeleteMapping("/refresh-cache")
    public Result<Void> refreshCache() {
        sysConfigService.refreshCache();
        return Result.success();
    }

}
