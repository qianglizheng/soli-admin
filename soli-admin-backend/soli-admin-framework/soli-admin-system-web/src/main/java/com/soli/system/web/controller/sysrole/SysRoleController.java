package com.soli.system.web.controller.sysrole;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysrole.SysRoleConverter;
import com.soli.system.service.sysrole.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 角色管理控制器
 *
 * @author lizhengqiang
 * @since 2026-03-14 20:15
*/
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/sys/role")
@AllArgsConstructor
@SecurityRequirement(name = "Authorization")
public class SysRoleController {

    @JsonFormat
    private final SysRoleService sysRoleService;

    private final SysRoleConverter sysRoleConverter;

    @Operation(summary = "新增角色")
    @PreAuthorize("hasAuthority('sys:role:create')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysRoleCreateRequest createRequest) {
        sysRoleService.create(sysRoleConverter.toDTO(createRequest));
        return Result.success();
    }

    @Operation(summary = "删除角色")
    @PreAuthorize("hasAuthority('sys:role:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysRoleService.remove(id);
        return Result.success();
    }

    @Operation(summary = "修改角色")
    @PreAuthorize("hasAuthority('sys:role:modify')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysRoleModifyRequest modifyRequest) {
        sysRoleService.modify(sysRoleConverter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "分页查询角色")
    @PreAuthorize("hasAuthority('sys:role:page')")
    @PostMapping("/page")
    public Result<PageResult<SysRoleDTO>> page(@RequestBody SysRoleQuery query) {
        return Result.success(sysRoleService.page(query));
    }

    @Operation(summary = "查询角色详情")
    @PreAuthorize("hasAuthority('sys:role:page')")
    @GetMapping("/{id}")
    public Result<SysRoleDTO> getById(@PathVariable Long id) {
        return Result.success(sysRoleService.getById(id).orElseThrow(() -> new BusinessException("指定角色不存在！")));
    }

}
