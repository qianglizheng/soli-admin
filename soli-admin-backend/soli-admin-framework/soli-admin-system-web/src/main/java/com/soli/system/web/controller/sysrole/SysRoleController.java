package com.soli.system.web.controller.sysrole;

import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysrole.SysRoleConverter;
import com.soli.system.service.sysrole.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-14 20:15
*/
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/sys/role")
@AllArgsConstructor
@SecurityRequirement(name = "Authorization")
public class SysRoleController {

    private final SysRoleService sysRoleService;

    private final SysRoleConverter sysRoleConverter;

    @Operation(summary = "新增角色")
    @PostMapping
    public void create(@RequestBody SysRoleCreateRequest createRequest) {
        sysRoleService.create(sysRoleConverter.toDTO(createRequest));
    }

    @Operation(summary = "查询角色详情")
    @GetMapping("/{id}")
    public SysRoleDTO getById(@PathVariable Long id) {
        return sysRoleService.getById(id);
    }

    @Operation(summary = "分页查询角色")
    @PostMapping("/page")
    public Result<PageResult<SysRoleDTO>> page(@RequestBody SysRoleQuery query) {
        return Result.success(sysRoleService.page(query));
    }

    @Operation(summary = "修改角色")
    @PutMapping("/{id}")
    public void modify(@PathVariable Long id, @RequestBody SysRoleModifyRequest modifyRequest) {
        sysRoleService.modify(id, sysRoleConverter.toDTO(modifyRequest));
    }

    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        sysRoleService.remove(id);
    }

}