package com.soli.system.web.controller.sysuser;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysuser.SysUserConverter;
import com.soli.system.service.sysuser.SysUserCreateRequest;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserModifyRequest;
import com.soli.system.service.sysuser.SysUserQuery;
import com.soli.system.service.sysuser.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
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

    @Operation(summary = "新增用户")
    @PreAuthorize("@moduleAccess.hasButton('sys_user', 'create')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysUserCreateRequest createRequest) {
        service.create(converter.toDTO(createRequest));
        return Result.success();
    }

    @Operation(summary = "删除用户")
    @PreAuthorize("@moduleAccess.hasButton('sys_user', 'remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        service.remove(id);
        return Result.success();
    }

    @Operation(summary = "修改用户")
    @PreAuthorize("@moduleAccess.hasButton('sys_user', 'modify')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysUserModifyRequest modifyRequest) {
        service.modify(converter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "分页查询用户")
    @PreAuthorize("@moduleAccess.hasModule('sys_user')")
    @PostMapping("/page")
    public Result<PageResult<SysUserDTO>> page(@RequestBody SysUserQuery query) {
        return Result.success(service.page(query));
    }

    @Operation(summary = "根据 ID 查询用户")
    @PreAuthorize("@moduleAccess.hasModule('sys_user')")
    @GetMapping("/{id}")
    public Result<SysUserDTO> getById(@PathVariable Long id) {
        SysUserDTO user = service.getById(id).orElseThrow(() -> new BusinessException("指定用户不存在！"));
        return Result.success(user);
    }
}
