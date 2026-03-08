package com.soli.system.web.controller.sysuser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.dto.SysUserDTO;
import com.soli.system.service.SysUserService;

import lombok.AllArgsConstructor;

/**
 * 系统用户控制器
 *
 * @author lizhengqiang
 * @since 2026-03-08 0:48
*/
@RestController
@RequestMapping("/sys-user")
@AllArgsConstructor
public class SysUserController {

    private final SysUserService service;

    @GetMapping("/{id}")
    public SysUserDTO findById(@PathVariable Long id) {
        SysUserDTO byId = service.getById(id);
        return byId;
    }

}