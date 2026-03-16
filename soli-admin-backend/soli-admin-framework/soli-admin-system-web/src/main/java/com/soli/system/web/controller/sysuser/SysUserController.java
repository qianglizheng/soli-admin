package com.soli.system.web.controller.sysuser;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * 系统用户控制器
 *
 * @author lizhengqiang
 * @since 2026-03-08 0:48
*/
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @Operation(summary = "根据用户ID查询用户")
    @PreAuthorize("hasAuthority('sys:user:list')")
    @GetMapping("/{id}")
    public SysUserDTO findById(@RequestHeader("Authorization") String accessToken, @PathVariable Long id) {
        return sysUserService.getById(id);
    }

    @Operation(summary = "添加系统用户")
    @PreAuthorize("hasAuthority('sys:user:add')")
    @PutMapping
    public void addSysUser(@RequestHeader("Authorization") String accessToken, @RequestBody SysUserDTO sysUser) {
        return;
    }

}