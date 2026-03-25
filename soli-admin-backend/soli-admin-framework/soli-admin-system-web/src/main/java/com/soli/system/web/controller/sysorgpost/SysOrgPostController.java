package com.soli.system.web.controller.sysorgpost;

import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysorgpost.SysOrgPostConverter;
import com.soli.system.core.service.impl.sysorgpost.SysOrgUnitConverter;
import com.soli.system.service.sysorgpost.SysOrgPostBindUserRequest;
import com.soli.system.service.sysorgpost.SysOrgPostCreateRequest;
import com.soli.system.service.sysorgpost.SysOrgPostDTO;
import com.soli.system.service.sysorgpost.SysOrgPostDetailDTO;
import com.soli.system.service.sysorgpost.SysOrgPostModifyRequest;
import com.soli.system.service.sysorgpost.SysOrgPostQuery;
import com.soli.system.service.sysorgpost.SysOrgPostService;
import com.soli.system.service.sysorgpost.SysOrgPostTreeNodeDTO;
import com.soli.system.service.sysorgpost.SysOrgPostUnbindUserRequest;
import com.soli.system.service.sysorgpost.SysOrgPostUserDTO;
import com.soli.system.service.sysorgpost.SysOrgPostUserQuery;
import com.soli.system.service.sysorgpost.SysOrgUnitCreateRequest;
import com.soli.system.service.sysorgpost.SysOrgUnitDTO;
import com.soli.system.service.sysorgpost.SysOrgUnitModifyRequest;
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

import java.util.List;

/**
 * 岗位管理控制器
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Tag(name = "岗位管理", description = "岗位管理相关接口")
@RestController
@RequestMapping("/sys/org-post")
@RequiredArgsConstructor
public class SysOrgPostController {

    private final SysOrgPostService service;

    private final SysOrgPostConverter converter;

    private final SysOrgUnitConverter sysOrgUnitConverter;

    @Operation(summary = "Query org post tree")
    @PreAuthorize("hasAuthority('sys:org-post:page')")
    @GetMapping("/tree")
    public Result<List<SysOrgPostTreeNodeDTO>> tree() {
        return Result.success(service.queryTreeList());
    }

    @Operation(summary = "根据 ID 查询岗位详情")
    @PreAuthorize("hasAuthority('sys:org-post:page')")
    @GetMapping("/{id}")
    public Result<SysOrgPostDetailDTO> getById(@PathVariable Long id) {
        return Result.success(service.queryDetailById(id));
    }

    @Operation(summary = "分页查询岗位")
    @PreAuthorize("hasAuthority('sys:org-post:page')")
    @PostMapping("/page")
    public Result<PageResult<SysOrgPostDTO>> page(@RequestBody SysOrgPostQuery query) {
        return Result.success(service.page(query));
    }

    @Operation(summary = "新增岗位")
    @PreAuthorize("hasAuthority('sys:org-post:create')")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SysOrgPostCreateRequest createRequest) {
        SysOrgPostDTO dto = converter.toDTO(createRequest);
        service.create(dto);
        return Result.success(dto.getId());
    }

    @Operation(summary = "新增组织单元")
    @PreAuthorize("hasAuthority('sys:org-post:create')")
    @PostMapping("/org-unit")
    public Result<Long> createOrgUnit(@Valid @RequestBody SysOrgUnitCreateRequest createRequest) {
        SysOrgUnitDTO dto = sysOrgUnitConverter.toDTO(createRequest);
        service.createOrgUnit(dto);
        return Result.success(dto.getId());
    }

    @Operation(summary = "根据 ID 查询组织单元详情")
    @PreAuthorize("hasAuthority('sys:org-post:page')")
    @GetMapping("/org-unit/{id}")
    public Result<SysOrgUnitDTO> getOrgUnitById(@PathVariable Long id) {
        return Result.success(service.queryOrgUnitById(id));
    }

    @Operation(summary = "修改组织单元")
    @PreAuthorize("hasAuthority('sys:org-post:modify')")
    @PutMapping("/org-unit")
    public Result<Void> modifyOrgUnit(@Valid @RequestBody SysOrgUnitModifyRequest modifyRequest) {
        service.modifyOrgUnit(sysOrgUnitConverter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "删除组织单元")
    @PreAuthorize("hasAuthority('sys:org-post:remove')")
    @DeleteMapping("/org-unit/{id}")
    public Result<Void> removeOrgUnit(@PathVariable Long id) {
        service.removeOrgUnit(id);
        return Result.success();
    }

    @Operation(summary = "修改岗位")
    @PreAuthorize("hasAuthority('sys:org-post:modify')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysOrgPostModifyRequest modifyRequest) {
        service.modify(converter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "删除岗位")
    @PreAuthorize("hasAuthority('sys:org-post:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        service.remove(id);
        return Result.success();
    }

    @Operation(summary = "分页查询岗位员工")
    @PreAuthorize("hasAuthority('sys:org-post:user:page')")
    @PostMapping("/user/page")
    public Result<PageResult<SysOrgPostUserDTO>> userPage(@RequestBody SysOrgPostUserQuery query) {
        return Result.success(service.queryUserPage(query));
    }

    @Operation(summary = "Query candidate users by page")
    @PreAuthorize("hasAuthority('sys:org-post:user:page')")
    @PostMapping("/user/options/page")
    public Result<PageResult<SysOrgPostUserDTO>> userOptionPage(@RequestBody SysOrgPostUserQuery query) {
        return Result.success(service.queryUserOptionPage(query));
    }

    @Operation(summary = "绑定岗位员工")
    @PreAuthorize("hasAuthority('sys:org-post:user:bind')")
    @PostMapping("/user/bind")
    public Result<Void> bindUsers(@Valid @RequestBody SysOrgPostBindUserRequest request) {
        service.bindUsers(request.getOrgPostId(), request.getUserIds());
        return Result.success();
    }

    @Operation(summary = "解绑岗位员工")
    @PreAuthorize("hasAuthority('sys:org-post:user:unbind')")
    @PostMapping("/user/unbind")
    public Result<Void> unbindUsers(@Valid @RequestBody SysOrgPostUnbindUserRequest request) {
        service.unbindUsers(request.getOrgPostId(), request.getUserIds());
        return Result.success();
    }

}
