package com.soli.system.web.controller.sysuser;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysuser.SysUserConverter;
import com.soli.system.service.sysrole.SysRoleDTO;
import com.soli.system.service.sysrole.SysRoleModifyRequest;
import com.soli.system.service.sysrole.SysRoleQuery;
import com.soli.system.service.sysuser.*;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 用户管理控制器
 *
 * @author lizhengqiang
 * @since 2026-03-08 0:48
*/
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService service;

    private final SysUserConverter converter;

    @Operation(summary = "添加用户")
    @PreAuthorize("hasAuthority('sys:user:create')")
    @PutMapping
    public void create(@RequestBody SysUserCreateRequest createRequest) {
        service.create(converter.toDTO(createRequest));
    }

    @Operation(summary = "删除用户")
    @PreAuthorize("hasAuthority('sys:user:remove')")
    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        service.remove(id);
    }

    @Operation(summary = "修改用户")
    @PreAuthorize("hasAuthority('sys:user:modify')")
    @PutMapping
    public void modify(@RequestBody SysUserModifyRequest modifyRequest) {
        service.modify(converter.toDTO(modifyRequest));
    }

    @Operation(summary = "分页查询用户")
    @PreAuthorize("hasAuthority('sys:user:page')")
    @PostMapping("/page")
    public Result<PageResult<SysUserDTO>> page(@RequestBody SysUserQuery query) {
        return Result.success(service.page(query));
    }

    @Operation(summary = "根据Id查询用户")
    @PreAuthorize("hasAuthority('sys:user:page')")
    @GetMapping("/{id}")
    public SysUserDTO getById(@PathVariable Long id) {
        return service.getById(id).orElseThrow(() -> new BusinessException("指定用户不存在！"));
    }

}