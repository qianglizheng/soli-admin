package com.soli.system.web.controller.sysdictdata;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.common.api.vo.Result;
import com.soli.system.core.service.impl.sysdictdata.SysDictDataConverter;
import com.soli.system.service.sysdictdata.SysDictDataCreateRequest;
import com.soli.system.service.sysdictdata.SysDictDataDTO;
import com.soli.system.service.sysdictdata.SysDictDataModifyRequest;
import com.soli.system.service.sysdictdata.SysDictDataQuery;
import com.soli.system.service.sysdictdata.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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

/**
 * 字典数据控制器
 *
 * @author lizhengqiang
 * @since 2026-03-21 23:45
 */
@Tag(name = "字典数据", description = "字典数据相关接口")
@RestController
@RequestMapping("/sys/dict/data")
@RequiredArgsConstructor
@SecurityRequirement(name = "Authorization")
public class SysDictDataController {

    private final SysDictDataService sysDictDataService;

    private final SysDictDataConverter sysDictDataConverter;

    @Operation(summary = "新增字典数据")
    @PreAuthorize("hasAuthority('sys:dict:create')")
    @PostMapping
    public Result<Void> create(@Valid @RequestBody SysDictDataCreateRequest createRequest) {
        sysDictDataService.create(sysDictDataConverter.toDTO(createRequest));
        return Result.success();
    }

    @Operation(summary = "删除字典数据")
    @PreAuthorize("hasAuthority('sys:dict:remove')")
    @DeleteMapping("/{id}")
    public Result<Void> remove(@PathVariable Long id) {
        sysDictDataService.remove(id);
        return Result.success();
    }

    @Operation(summary = "修改字典数据")
    @PreAuthorize("hasAuthority('sys:dict:modify')")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody SysDictDataModifyRequest modifyRequest) {
        sysDictDataService.modify(sysDictDataConverter.toDTO(modifyRequest));
        return Result.success();
    }

    @Operation(summary = "分页查询字典数据")
    @PreAuthorize("hasAuthority('sys:dict:page')")
    @PostMapping("/page")
    public Result<PageResult<SysDictDataDTO>> page(@RequestBody SysDictDataQuery query) {
        return Result.success(sysDictDataService.page(query));
    }

    @Operation(summary = "查询字典数据详情")
    @PreAuthorize("hasAuthority('sys:dict:page')")
    @GetMapping("/{id}")
    public Result<SysDictDataDTO> getById(@PathVariable Long id) {
        return Result.success(sysDictDataService.getById(id).orElseThrow(() -> new BusinessException("指定字典数据不存在！")));
    }

}
