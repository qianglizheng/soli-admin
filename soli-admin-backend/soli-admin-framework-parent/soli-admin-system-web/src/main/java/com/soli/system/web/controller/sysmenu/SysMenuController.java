package com.soli.system.web.controller.sysmenu;

import java.util.List;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.service.sysmenu.SysMenuDTO;
import com.soli.system.service.sysmenu.SysMenuService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

/**
 * @author lizhengqiang
 * @since 2026-03-15 12:50
*/
@Tag(name = "菜单管理", description = "菜单管理相关接口")
@RestController
@RequestMapping("/sys/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService sysMenuService;

    @Operation(summary = "获取树形菜单")
    @GetMapping("tree")
    public List<SysMenuDTO> getTreeList(@RequestHeader("Authorization") String accessToken, @AuthenticationPrincipal Long userId) {
        return sysMenuService.queryTreeList(userId);
    }

}