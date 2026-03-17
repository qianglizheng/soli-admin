package com.soli.system.web.controller.sysmenu;

import java.util.List;

import com.soli.common.api.vo.Result;
import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.soli.system.core.service.impl.sysmenu.SysMenuConverter;
import com.soli.system.service.sysmenu.SysMenuCreateRequest;
import com.soli.system.service.sysmenu.SysMenuDTO;
import com.soli.system.service.sysmenu.SysMenuService;
import com.soli.system.service.sysmenu.SysMenuModifyRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * @author lizhengqiang
 * @since 2026-03-15 12:50
*/
@Tag(name = "菜单管理", description = "菜单管理相关接口")
@RestController
@RequestMapping("/sys/menu")
@AllArgsConstructor
@SecurityRequirement(name = "Authorization")
public class SysMenuController {

    private final SysMenuService sysMenuService;

    private final SysMenuConverter sysMenuConverter;

    @Operation(summary = "获取树形菜单")
    @GetMapping("tree")
    public Result<List<SysMenuDTO>> getTreeList(@AuthenticationPrincipal Long userId) {
        return Result.success(sysMenuService.queryTreeList(userId));
    }

    @Operation(summary = "新增菜单")
    @PostMapping
    public void create(@RequestBody SysMenuCreateRequest createRequest) {
        sysMenuService.create(sysMenuConverter.toDTO(createRequest));
    }

    @Operation(summary = "修改菜单")
    @PutMapping
    public void modify(@RequestBody SysMenuModifyRequest updateRequest) {
        sysMenuService.modify(sysMenuConverter.toDTO(updateRequest));
    }

    @Operation(summary = "删除菜单")
    @DeleteMapping("/{id}")
    public void remove(@PathVariable Long id) {
        sysMenuService.remove(id);
    }

}
