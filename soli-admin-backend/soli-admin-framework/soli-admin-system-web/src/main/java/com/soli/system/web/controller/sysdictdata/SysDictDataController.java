package com.soli.system.web.controller.sysdictdata;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.common.web.controller.BaseController;
import com.soli.system.core.service.impl.sysdictdata.SysDictDataConverter;
import com.soli.system.service.sysdictdata.SysDictDataCreateRequest;
import com.soli.system.service.sysdictdata.SysDictDataDTO;
import com.soli.system.service.sysdictdata.SysDictDataModifyRequest;
import com.soli.system.service.sysdictdata.SysDictDataQuery;
import com.soli.system.service.sysdictdata.SysDictDataService;
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
 * 字典数据控制器
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:45
 */
@Tag(name = "字典数据", description = "字典数据相关接口")
@RestController
@RequestMapping("/sys/dict/data")
@SecurityRequirement(name = "Authorization")
public class SysDictDataController extends BaseController {

    private final SysDictDataService sysDictDataService;

    private final SysDictDataConverter sysDictDataConverter;

    public SysDictDataController(final SysDictDataService sysDictDataService,
                                 final SysDictDataConverter sysDictDataConverter,
                                 final SysModuleContextService sysModuleContextService) {
        super(sysModuleContextService);
        this.sysDictDataService = sysDictDataService;
        this.sysDictDataConverter = sysDictDataConverter;
    }

    @Operation(summary = "新增字典数据")
    @PreAuthorize("@moduleAccess.hasStateButton('sys_dict_data', 'create', 'create')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictDataCreateRequest createRequest) {
        sysDictDataService.create(sysDictDataConverter.toDTO(createRequest));
        return Result.success();
    }

    @Operation(summary = "删除字典数据")
    @PreAuthorize("@moduleAccess.hasButton('sys_dict_data', 'remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysDictDataService.remove(id);
        return Result.success();
    }

    @Operation(summary = "修改字典数据")
    @PreAuthorize("@moduleAccess.hasStateButton('sys_dict_data', 'modify', 'edit')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysDictDataModifyRequest modifyRequest) {
        sysDictDataService.modify(sysDictDataConverter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "分页查询字典数据")
    @PreAuthorize("@moduleAccess.hasModule('sys_dict_data')")
    @PostMapping("/page")
    public Result<PageResult<SysDictDataDTO>> page(@RequestBody SysDictDataQuery query) {
        return Result.success(sysDictDataService.page(query));
    }

    @Operation(summary = "查询字典数据模块上下文")
    @PreAuthorize("@moduleAccess.hasModule('sys_dict_data')")
    @GetMapping("/context")
    public Result<SysModuleContextDTO> context(@RequestParam(required = false) String stateCode) {
        return Result.success(buildModuleContext("sys_dict_data", stateCode));
    }

    @Operation(summary = "查询字典数据详情")
    @PreAuthorize("@moduleAccess.hasModule('sys_dict_data')")
    @GetMapping("/{id}")
    public Result<SysDictDataDTO> getById(@PathVariable Long id) {
        return Result.success(sysDictDataService.getById(id).orElseThrow(() -> new BusinessException("指定字典数据不存在！")));
    }

}
