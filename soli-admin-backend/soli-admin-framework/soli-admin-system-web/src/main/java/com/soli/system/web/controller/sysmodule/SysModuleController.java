package com.soli.system.web.controller.sysmodule;

import com.soli.common.api.vo.Result;
import com.soli.common.web.controller.BaseController;
import com.soli.common.web.security.jwt.CompanyContextHolder;
import com.soli.system.core.service.impl.sysmodule.SysModuleConverter;
import com.soli.system.service.sysmodule.SysModuleButtonCreateRequest;
import com.soli.system.service.sysmodule.SysModuleButtonDTO;
import com.soli.system.service.sysmodule.SysModuleButtonModifyRequest;
import com.soli.system.service.sysmodule.SysModuleComponentCreateRequest;
import com.soli.system.service.sysmodule.SysModuleComponentDTO;
import com.soli.system.service.sysmodule.SysModuleComponentModifyRequest;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import com.soli.system.service.sysmodule.SysModuleCreateRequest;
import com.soli.system.service.sysmodule.SysModuleDTO;
import com.soli.system.service.sysmodule.SysModuleDetailDTO;
import com.soli.system.service.sysmodule.SysModuleFieldCreateRequest;
import com.soli.system.service.sysmodule.SysModuleFieldDTO;
import com.soli.system.service.sysmodule.SysModuleFieldModifyRequest;
import com.soli.system.service.sysmodule.SysModuleModifyRequest;
import com.soli.system.service.sysmodule.SysModuleService;
import com.soli.system.service.sysmodule.SysModuleTreeNodeDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
 * Module controller.
 *
 * @author lizhengqiang
 * @since 2026-03-28 14:55
 */
@Tag(name = "Module", description = "Module management endpoints")
@RestController
@RequestMapping("/sys/module")
public class SysModuleController extends BaseController {

    private final SysModuleService service;

    private final SysModuleConverter converter;

    public SysModuleController(final SysModuleService service,
                               final SysModuleConverter converter,
                               final SysModuleContextService sysModuleContextService) {
        super(sysModuleContextService);
        this.service = service;
        this.converter = converter;
    }

    @Operation(summary = "Query module tree")
    @PreAuthorize("@moduleAccess.hasModule('sys_module')")
    @GetMapping("/tree")
    public Result<List<SysModuleTreeNodeDTO>> tree() {
        return Result.success(service.queryAllTreeList());
    }

    @Operation(summary = "Query current user nav tree")
    @GetMapping("/nav-tree")
    public Result<List<SysModuleTreeNodeDTO>> navTree(@AuthenticationPrincipal Long userId) {
        return Result.success(service.queryNavTree(userId, CompanyContextHolder.getCurrentCompanyId()));
    }

    @Operation(summary = "Query module detail")
    @PreAuthorize("@moduleAccess.hasModule('sys_module')")
    @GetMapping("/{id}")
    public Result<SysModuleDetailDTO> getById(@PathVariable Long id) {
        return Result.success(service.queryManageDetailById(id));
    }

    @Operation(summary = "Preview runtime module context")
    @PreAuthorize("@moduleAccess.hasModule('sys_module')")
    @GetMapping("/{id}/context-preview")
    public Result<SysModuleContextDTO> contextPreview(@PathVariable Long id) {
        SysModuleDetailDTO moduleDetail = service.queryManageDetailById(id);
        return Result.success(buildModuleContext(moduleDetail.getModuleCode()));
    }

    @Operation(summary = "Create module")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'create')")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody SysModuleCreateRequest createRequest) {
        SysModuleDTO dto = converter.toDTO(createRequest);
        service.create(dto);
        return Result.success(dto.getId());
    }

    @Operation(summary = "Modify module")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'modify')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysModuleModifyRequest modifyRequest) {
        service.modify(converter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "Remove module")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        service.remove(id);
        return Result.success();
    }

    @Operation(summary = "Create module component")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'componentCreate')")
    @PostMapping("/component")
    public Result<Long> createComponent(@Valid @RequestBody SysModuleComponentCreateRequest createRequest) {
        SysModuleComponentDTO dto = converter.toDTO(createRequest);
        service.createComponent(dto);
        return Result.success(dto.getId());
    }

    @Operation(summary = "Modify module component")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'componentModify')")
    @PutMapping("/component")
    public Result<Void> modifyComponent(@Valid @RequestBody SysModuleComponentModifyRequest modifyRequest) {
        service.modifyComponent(converter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "Remove module component")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'componentRemove')")
    @DeleteMapping("/component/{id}")
    public Result<Void> removeComponent(@PathVariable Long id) {
        service.removeComponent(id);
        return Result.success();
    }

    @Operation(summary = "Create module field")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'fieldCreate')")
    @PostMapping("/field")
    public Result<Long> createField(@Valid @RequestBody SysModuleFieldCreateRequest createRequest) {
        SysModuleFieldDTO dto = converter.toDTO(createRequest);
        service.createField(dto);
        return Result.success(dto.getId());
    }

    @Operation(summary = "Modify module field")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'fieldModify')")
    @PutMapping("/field")
    public Result<Void> modifyField(@Valid @RequestBody SysModuleFieldModifyRequest modifyRequest) {
        service.modifyField(converter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "Remove module field")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'fieldRemove')")
    @DeleteMapping("/field/{id}")
    public Result<Void> removeField(@PathVariable Long id) {
        service.removeField(id);
        return Result.success();
    }

    @Operation(summary = "Create module button")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'buttonCreate')")
    @PostMapping("/button")
    public Result<Long> createButton(@Valid @RequestBody SysModuleButtonCreateRequest createRequest) {
        SysModuleButtonDTO dto = converter.toDTO(createRequest);
        service.createButton(dto);
        return Result.success(dto.getId());
    }

    @Operation(summary = "Modify module button")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'buttonModify')")
    @PutMapping("/button")
    public Result<Void> modifyButton(@Valid @RequestBody SysModuleButtonModifyRequest modifyRequest) {
        service.modifyButton(converter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "Remove module button")
    @PreAuthorize("@moduleAccess.hasButton('sys_module', 'buttonRemove')")
    @DeleteMapping("/button/{id}")
    public Result<Void> removeButton(@PathVariable Long id) {
        service.removeButton(id);
        return Result.success();
    }

}
