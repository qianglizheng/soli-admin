package com.soli.system.web.controller.sysrole;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.service.sysrole.SysRoleService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-14 20:15
*/
@Tag(name = "系统角色", description = "系统角色相关接口")
@RestController
@RequestMapping("/sys/role")
@AllArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

}