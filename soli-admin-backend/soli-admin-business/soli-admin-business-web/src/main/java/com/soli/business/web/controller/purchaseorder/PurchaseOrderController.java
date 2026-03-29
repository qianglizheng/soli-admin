package com.soli.business.web.controller.purchaseorder;

import com.soli.business.service.purchaseorder.PurchaseOrderDetailDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderPageDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderPageQuery;
import com.soli.business.service.purchaseorder.PurchaseOrderSaveRequest;
import com.soli.business.service.purchaseorder.PurchaseOrderService;
import com.soli.common.api.vo.Result;
import com.soli.common.web.controller.BaseController;
import com.soli.system.service.sysmodule.SysModuleContextDTO;
import com.soli.system.service.sysmodule.SysModuleContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 采购订单控制器
 *
 * @author lizhengqiang
 * @since 2026-03-29 22:40
 */
@Tag(name = "采购订单", description = "采购订单相关接口")
@RestController
@RequestMapping("/purchase/order")
public class PurchaseOrderController extends BaseController {

    private static final String MODULE_CODE = "purchase_order";

    private final PurchaseOrderService service;

    public PurchaseOrderController(final PurchaseOrderService service,
                                   final SysModuleContextService sysModuleContextService) {
        super(sysModuleContextService);
        this.service = service;
    }

    @Operation(summary = "查询采购订单分页列表")
    @PreAuthorize("@moduleAccess.hasModule('purchase_order')")
    @PostMapping("/page")
    public Result<PurchaseOrderPageDTO> page(@RequestBody(required = false) final PurchaseOrderPageQuery query) {
        return Result.success(service.queryPage(query, currentCompanyId()));
    }

    @Operation(summary = "查询采购订单详情")
    @PreAuthorize("@moduleAccess.hasModule('purchase_order')")
    @GetMapping("/{id}")
    public Result<PurchaseOrderDetailDTO> detail(@PathVariable final Long id) {
        return Result.success(service.queryDetail(id, currentCompanyId()));
    }

    @Operation(summary = "查询采购订单模块上下文")
    @PreAuthorize("@moduleAccess.hasModule('purchase_order')")
    @GetMapping("/context")
    public Result<SysModuleContextDTO> context(@RequestParam(required = false) final String stateCode) {
        return Result.success(buildModuleContext(MODULE_CODE, stateCode));
    }

    @Operation(summary = "新增采购订单")
    @PreAuthorize("@purchaseOrderAccess.hasStateButton('save', 'unaudited')")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody final PurchaseOrderSaveRequest request) {
        return Result.success(service.create(request, currentUserId(), currentCompanyId()));
    }

    @Operation(summary = "修改采购订单")
    @PreAuthorize("@purchaseOrderAccess.hasOrderButton('save', #request.id)")
    @PutMapping
    public Result<Void> modify(@Valid @RequestBody final PurchaseOrderSaveRequest request) {
        service.modify(request, currentUserId(), currentCompanyId());
        return Result.success();
    }

    @Operation(summary = "执行采购订单动作")
    @PreAuthorize("@purchaseOrderAccess.hasOrderButton(#actionCode, #id)")
    @PostMapping("/{id}/action/{actionCode}")
    public Result<Void> executeAction(@PathVariable final Long id, @PathVariable final String actionCode) {
        service.executeAction(id, actionCode, currentUserId(), currentCompanyId());
        return Result.success();
    }

}
