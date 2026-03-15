package com.soli.system.web.controller.sysrole;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.service.sysrole.SysRoleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-14 20:15
*/
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/sys/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

}