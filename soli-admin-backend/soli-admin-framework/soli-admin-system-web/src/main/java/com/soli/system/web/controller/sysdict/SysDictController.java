package com.soli.system.web.controller.sysdict;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.common.web.controller.BaseController;
import com.soli.system.core.service.impl.sysdict.SysDictConverter;
import com.soli.system.service.sysdict.SysDictCreateRequest;
import com.soli.system.service.sysdict.SysDictDTO;
import com.soli.system.service.sysdict.SysDictModifyRequest;
import com.soli.system.service.sysdict.SysDictQuery;
import com.soli.system.service.sysdict.SysDictService;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 字典管理控制器
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:42
 */
@Tag(name = "字典管理", description = "字典管理相关接口")
@RestController
@RequestMapping("/sys/dict")
@SecurityRequirement(name = "Authorization")
public class SysDictController extends BaseController {

    private final SysDictService sysDictService;

    private final SysDictConverter sysDictConverter;

    public SysDictController(final SysDictService sysDictService,
                             final SysDictConverter sysDictConverter,
                             final SysModuleContextService sysModuleContextService) {
        super(sysModuleContextService);
        this.sysDictService = sysDictService;
        this.sysDictConverter = sysDictConverter;
    }

    @Operation(summary = "新增字典类型")
    @PreAuthorize("@moduleAccess.hasStateButton('sys_dict', 'create', 'create')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictCreateRequest createRequest) {
        sysDictService.create(sysDictConverter.toDTO(createRequest));
        return Result.success();
    }

    @Operation(summary = "删除字典类型")
    @PreAuthorize("@moduleAccess.hasButton('sys_dict', 'remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysDictService.remove(id);
        return Result.success();
    }

    @Operation(summary = "修改字典类型")
    @PreAuthorize("@moduleAccess.hasStateButton('sys_dict', 'modify', 'edit')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysDictModifyRequest modifyRequest) {
        sysDictService.modify(sysDictConverter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "分页查询字典类型")
    @PreAuthorize("@moduleAccess.hasModule('sys_dict')")
    @PostMapping("/page")
    public Result<PageResult<SysDictDTO>> page(@RequestBody SysDictQuery query) {
        return Result.success(sysDictService.page(query));
    }

    @Operation(summary = "查询字典管理模块上下文")
    @PreAuthorize("@moduleAccess.hasModule('sys_dict')")
    @GetMapping("/context")
    public Result<SysModuleContextDTO> context(@RequestParam(required = false) String stateCode) {
        return Result.success(buildModuleContext("sys_dict", stateCode));
    }

    @Operation(summary = "查询字典类型详情")
    @PreAuthorize("@moduleAccess.hasModule('sys_dict')")
    @GetMapping("/{id}")
    public Result<SysDictDTO> getById(@PathVariable Long id) {
        return Result.success(sysDictService.getById(id).orElseThrow(() -> new BusinessException("指定字典不存在！")));
    }

}
