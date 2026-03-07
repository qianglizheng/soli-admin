package com.soli.system.web.controller.sysuser;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.core.entity.sysuser.SysUserEntity;
import com.soli.system.core.service.sysuser.SysUserService;

import lombok.AllArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-08 0:48
*/
@RestController
@RequestMapping("/sys-user")
@AllArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/insert")
    public void insert() {
        SysUserEntity user = new SysUserEntity();
        user.setUsername("admin");
        user.setPassword("123456");

        sysUserService.save(user);
    }
}
