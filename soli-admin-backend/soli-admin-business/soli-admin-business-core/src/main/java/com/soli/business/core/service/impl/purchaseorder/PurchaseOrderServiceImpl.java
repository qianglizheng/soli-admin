package com.soli.business.core.service.impl.purchaseorder;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.yitter.idgen.YitIdHelper;
import com.soli.business.core.mapper.PurchaseOrderMapper;
import com.soli.business.service.purchaseorder.PurchaseOrderActionEnum;
import com.soli.business.service.purchaseorder.PurchaseOrderActivityDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderActivityTypeEnum;
import com.soli.business.service.purchaseorder.PurchaseOrderAttachmentDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderDetailDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderHeaderDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderItemDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderListItemDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderOverviewCardDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderPageDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderPageQuery;
import com.soli.business.service.purchaseorder.PurchaseOrderSaveRequest;
import com.soli.business.service.purchaseorder.PurchaseOrderSaveRequest.PurchaseOrderItemSaveRequest;
import com.soli.business.service.purchaseorder.PurchaseOrderService;
import com.soli.business.service.purchaseorder.PurchaseOrderSourceDTO;
import com.soli.business.service.purchaseorder.PurchaseOrderStatusEnum;
import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.PageResult;
import com.soli.system.core.mapper.SysUserMapper;
import com.soli.system.core.service.impl.sysuser.SysUserEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 采购订单服务实现
 *
 * @author lizhengqiang
 * @since 2026-03-29 22:15
 */
@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private static final PurchaseOrderStatusEnum STATUS_UNAUDITED = PurchaseOrderStatusEnum.UNAUDITED;

    private static final PurchaseOrderStatusEnum STATUS_PRE_AUDITED = PurchaseOrderStatusEnum.PRE_AUDITED;

    private static final PurchaseOrderStatusEnum STATUS_AUDITED = PurchaseOrderStatusEnum.AUDITED;

    private static final PurchaseOrderStatusEnum STATUS_SHIPPED = PurchaseOrderStatusEnum.SHIPPED;

    private static final PurchaseOrderStatusEnum STATUS_COMPLETED = PurchaseOrderStatusEnum.COMPLETED;

    private static final String BILL_TYPE_NAME = "采购订单";

    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static final Map<PurchaseOrderStatusEnum, String> STATUS_NAME_MAP = new LinkedHashMap<>();

    private static final Map<String, String> SUPPLIER_NAME_MAP = new LinkedHashMap<>();

    private static final Map<Long, String> WAREHOUSE_NAME_MAP = new LinkedHashMap<>();

    private static final Map<String, ActionDefinition> ACTION_DEFINITION_MAP = new LinkedHashMap<>();

    static {
        STATUS_NAME_MAP.put(STATUS_UNAUDITED, "未审核");
        STATUS_NAME_MAP.put(STATUS_PRE_AUDITED, "待审核");
        STATUS_NAME_MAP.put(STATUS_AUDITED, "已审核");
        STATUS_NAME_MAP.put(STATUS_SHIPPED, "已发运");
        STATUS_NAME_MAP.put(STATUS_COMPLETED, "已完成");

        // 演示数据字典先在本地固定维护。
        SUPPLIER_NAME_MAP.put("1", "华为技术有限公司");
        SUPPLIER_NAME_MAP.put("2", "小米通讯有限公司");
        SUPPLIER_NAME_MAP.put("3", "宁德时代新能源科技股份有限公司");

        WAREHOUSE_NAME_MAP.put(1L, "深圳一号仓");
        WAREHOUSE_NAME_MAP.put(2L, "广州二号仓");
        WAREHOUSE_NAME_MAP.put(3L, "上海中心仓");

        ACTION_DEFINITION_MAP.put("submit", new ActionDefinition(STATUS_UNAUDITED, STATUS_PRE_AUDITED, "提交", "warning"));
        ACTION_DEFINITION_MAP.put("audit", new ActionDefinition(STATUS_PRE_AUDITED, STATUS_AUDITED, "审核", "success"));
        ACTION_DEFINITION_MAP.put("ship", new ActionDefinition(STATUS_AUDITED, STATUS_SHIPPED, "发运", "success"));
        ACTION_DEFINITION_MAP.put("complete", new ActionDefinition(STATUS_SHIPPED, STATUS_COMPLETED, "完成", "success"));
    }

    private final PurchaseOrderMapper purchaseOrderMapper;

    private final SysUserMapper sysUserMapper;

    @Override
    public PurchaseOrderPageDTO queryPage(final PurchaseOrderPageQuery query, final Long companyId) {
        assertCompanyId(companyId);
        PurchaseOrderPageQuery actualQuery = query == null ? new PurchaseOrderPageQuery() : query;
        actualQuery.setCompanyId(companyId);

        PageHelper.startPage(actualQuery.getPageNum(), actualQuery.getPageSize());
        List<PurchaseOrderEntity> entityList = purchaseOrderMapper.selectPage(actualQuery);
        PageInfo<PurchaseOrderEntity> pageInfo = new PageInfo<>(entityList);

        List<PurchaseOrderListItemDTO> dtoList = entityList.stream()
                .map(this::toListItemDTO)
                .toList();

        PurchaseOrderPageDTO result = new PurchaseOrderPageDTO();
        result.setOverviewCards(buildOverviewCards(companyId));
        result.setPage(PageResult.of(pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal(), dtoList));
        return result;
    }

    @Override
    public PurchaseOrderDetailDTO queryDetail(final Long id, final Long companyId) {
        PurchaseOrderEntity entity = getRequiredEntity(id, companyId);

        PurchaseOrderDetailDTO detailDTO = new PurchaseOrderDetailDTO();
        detailDTO.setHeader(toHeaderDTO(entity));
        detailDTO.setItems(purchaseOrderMapper.selectItemListByOrderId(entity.getId()).stream()
                .map(this::toItemDTO)
                .toList());
        detailDTO.setSourceBills(purchaseOrderMapper.selectSourceListByOrderId(entity.getId()).stream()
                .map(this::toSourceDTO)
                .toList());
        detailDTO.setAttachments(purchaseOrderMapper.selectAttachmentListByOrderId(entity.getId()).stream()
                .map(this::toAttachmentDTO)
                .toList());
        detailDTO.setActivities(purchaseOrderMapper.selectActivityListByOrderId(entity.getId()).stream()
                .map(this::toActivityDTO)
                .toList());
        return detailDTO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(final PurchaseOrderSaveRequest request, final Long userId, final Long companyId) {
        assertCompanyId(companyId);
        validateSaveRequest(request);

        String operatorName = resolveUserDisplayName(userId);
        LocalDateTime now = LocalDateTime.now();
        OrderAmount amount = calculateAmount(request.getItems());

        PurchaseOrderEntity entity = new PurchaseOrderEntity();
        entity.setId(YitIdHelper.nextId());
        entity.setCompanyId(companyId);
        entity.setBillNo(generateBillNo(entity.getId(), request.getBillDate()));
        entity.setBillDate(parseDate(request.getBillDate(), "单据日期格式不正确"));
        entity.setSupplierId(request.getSupplierId());
        entity.setSupplierName(resolveSupplierName(request.getSupplierId()));
        entity.setSettleType(request.getSettleType());
        entity.setWarehouseId(request.getWarehouseId());
        entity.setWarehouseName(resolveWarehouseName(request.getWarehouseId()));
        entity.setUserName(request.getUserName());
        entity.setCurrency(request.getCurrency());
        entity.setRemark(request.getRemark());
        entity.setStatus(PurchaseOrderStatusEnum.UNAUDITED);
        entity.setCreateUserId(userId);
        entity.setCreateByName(operatorName);
        entity.setTotalQty(amount.totalQty());
        entity.setNetAmount(amount.netAmount());
        entity.setTaxAmount(amount.taxAmount());
        entity.setTotalAmount(amount.totalAmount());
        entity.setCreateBy(operatorName);
        entity.setCreateTime(now);
        entity.setUpdateBy(operatorName);
        entity.setUpdateTime(now);
        if (purchaseOrderMapper.insert(entity) == 0) {
            throw new BusinessException("采购订单创建失败");
        }

        replaceItemList(entity.getId(), request.getItems(), operatorName, now);
        insertActivity(entity.getId(),
                "create",
                "创建",
                userId,
                operatorName,
                "创建采购订单 " + entity.getBillNo(),
                "primary",
                now);
        return entity.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modify(final PurchaseOrderSaveRequest request, final Long userId, final Long companyId) {
        assertCompanyId(companyId);
        validateSaveRequest(request);
        if (request.getId() == null) {
            throw new BusinessException("采购订单 ID 不能为空");
        }

        PurchaseOrderEntity existingEntity = getRequiredEntity(request.getId(), companyId);
        if (!STATUS_UNAUDITED.equals(existingEntity.getStatus())) {
            throw new BusinessException("当前状态不允许编辑采购订单");
        }

        String operatorName = resolveUserDisplayName(userId);
        LocalDateTime now = LocalDateTime.now();
        OrderAmount amount = calculateAmount(request.getItems());

        PurchaseOrderEntity entity = new PurchaseOrderEntity();
        entity.setId(existingEntity.getId());
        entity.setBillDate(parseDate(request.getBillDate(), "单据日期格式不正确"));
        entity.setSupplierId(request.getSupplierId());
        entity.setSupplierName(resolveSupplierName(request.getSupplierId()));
        entity.setSettleType(request.getSettleType());
        entity.setWarehouseId(request.getWarehouseId());
        entity.setWarehouseName(resolveWarehouseName(request.getWarehouseId()));
        entity.setUserName(request.getUserName());
        entity.setCurrency(request.getCurrency());
        entity.setRemark(request.getRemark());
        entity.setTotalQty(amount.totalQty());
        entity.setNetAmount(amount.netAmount());
        entity.setTaxAmount(amount.taxAmount());
        entity.setTotalAmount(amount.totalAmount());
        entity.setUpdateBy(operatorName);
        entity.setUpdateTime(now);
        if (purchaseOrderMapper.update(entity) == 0) {
            throw new BusinessException("采购订单更新失败");
        }

        replaceItemList(existingEntity.getId(), request.getItems(), operatorName, now);
        insertActivity(existingEntity.getId(),
                "modify",
                "修改",
                userId,
                operatorName,
                "修改采购订单 " + existingEntity.getBillNo(),
                "primary",
                now);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeAction(final Long id, final String actionCode, final Long userId, final Long companyId) {
        PurchaseOrderEntity entity = getRequiredEntity(id, companyId);
        ActionDefinition actionDefinition = ACTION_DEFINITION_MAP.get(actionCode);
        if (actionDefinition == null) {
            throw new BusinessException("不支持的采购订单动作");
        }
        if (!actionDefinition.fromStatus().equals(entity.getStatus())) {
            throw new BusinessException("当前状态不允许执行该操作");
        }

        String operatorName = resolveUserDisplayName(userId);
        LocalDateTime now = LocalDateTime.now();
        PurchaseOrderEntity updateEntity = new PurchaseOrderEntity();
        updateEntity.setId(entity.getId());
        updateEntity.setStatus(actionDefinition.toStatus());
        updateEntity.setUpdateBy(operatorName);
        updateEntity.setUpdateTime(now);
        if ("submit".equals(actionCode)) {
            updateEntity.setSubmitTime(now);
        }
        if ("audit".equals(actionCode)) {
            updateEntity.setAuditUserId(userId);
            updateEntity.setAuditUserName(operatorName);
            updateEntity.setAuditTime(now);
        }
        if ("ship".equals(actionCode)) {
            updateEntity.setShipTime(now);
        }
        if ("complete".equals(actionCode)) {
            updateEntity.setCompleteTime(now);
        }
        if (purchaseOrderMapper.update(updateEntity) == 0) {
            throw new BusinessException("采购订单状态更新失败");
        }

        insertActivity(entity.getId(),
                actionCode,
                actionDefinition.actionName(),
                userId,
                operatorName,
                actionDefinition.actionName() + "采购订单 " + entity.getBillNo(),
                actionDefinition.activityType(),
                now);
    }

    /**
     * 构建列表上方的概览卡片
     */
    private List<PurchaseOrderOverviewCardDTO> buildOverviewCards(final Long companyId) {
        PurchaseOrderOverviewModel overviewModel = purchaseOrderMapper.selectOverview(companyId);
        if (overviewModel == null) {
            overviewModel = new PurchaseOrderOverviewModel();
            overviewModel.setTotalCount(0L);
            overviewModel.setUnauditedCount(0L);
            overviewModel.setPreAuditedCount(0L);
            overviewModel.setCompletedCount(0L);
            overviewModel.setTotalAmount(ZERO);
        }

        List<PurchaseOrderOverviewCardDTO> cardList = new ArrayList<>();
        cardList.add(buildOverviewCard("totalCount",
                "订单总数",
                String.valueOf(defaultLong(overviewModel.getTotalCount())),
                "document",
                "primary",
                "全部采购订单"));
        cardList.add(buildOverviewCard("preAuditedCount",
                "待审核",
                String.valueOf(defaultLong(overviewModel.getPreAuditedCount())),
                "clock",
                "warning",
                "等待审核处理"));
        cardList.add(buildOverviewCard("totalAmount",
                "订单金额",
                formatDecimal(overviewModel.getTotalAmount()),
                "money",
                "danger",
                "价税合计"));
        cardList.add(buildOverviewCard("completedCount",
                "已完成",
                String.valueOf(defaultLong(overviewModel.getCompletedCount())),
                "finished",
                "success",
                "已完成业务单据"));
        return cardList;
    }

    /**
     * 构建单个概览卡片
     */
    private PurchaseOrderOverviewCardDTO buildOverviewCard(final String key,
                                                           final String label,
                                                           final String value,
                                                           final String icon,
                                                           final String theme,
                                                           final String footerText) {
        PurchaseOrderOverviewCardDTO cardDTO = new PurchaseOrderOverviewCardDTO();
        cardDTO.setKey(key);
        cardDTO.setLabel(label);
        cardDTO.setValue(value);
        cardDTO.setIcon(icon);
        cardDTO.setTheme(theme);
        cardDTO.setFooterText(footerText);
        return cardDTO;
    }

    private PurchaseOrderListItemDTO toListItemDTO(final PurchaseOrderEntity entity) {
        PurchaseOrderListItemDTO dto = new PurchaseOrderListItemDTO();
        dto.setId(entity.getId());
        dto.setBillNo(entity.getBillNo());
        dto.setBillType(BILL_TYPE_NAME);
        dto.setSupplierId(entity.getSupplierId());
        dto.setSupplierName(entity.getSupplierName());
        dto.setBillDate(formatDate(entity.getBillDate()));
        dto.setUserName(entity.getUserName());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setWarehouseName(entity.getWarehouseName());
        dto.setTotalAmount(defaultDecimal(entity.getTotalAmount()));
        dto.setNetAmount(defaultDecimal(entity.getNetAmount()));
        dto.setTaxAmount(defaultDecimal(entity.getTaxAmount()));
        dto.setTotalQty(defaultDecimal(entity.getTotalQty()));
        dto.setStatus(entity.getStatus());
        dto.setStatusName(resolveStatusName(entity.getStatus()));
        return dto;
    }

    private PurchaseOrderHeaderDTO toHeaderDTO(final PurchaseOrderEntity entity) {
        PurchaseOrderHeaderDTO dto = new PurchaseOrderHeaderDTO();
        dto.setId(entity.getId());
        dto.setBillNo(entity.getBillNo());
        dto.setBillDate(formatDate(entity.getBillDate()));
        dto.setStatus(entity.getStatus());
        dto.setStatusName(resolveStatusName(entity.getStatus()));
        dto.setSupplierId(entity.getSupplierId());
        dto.setSupplierName(entity.getSupplierName());
        dto.setSettleType(entity.getSettleType());
        dto.setDeptId("");
        dto.setUserName(entity.getUserName());
        dto.setWarehouseId(entity.getWarehouseId());
        dto.setWarehouseName(entity.getWarehouseName());
        dto.setCurrency(entity.getCurrency());
        dto.setRemark(entity.getRemark());
        dto.setCreateByName(entity.getCreateByName());
        return dto;
    }

    private PurchaseOrderItemDTO toItemDTO(final PurchaseOrderItemEntity entity) {
        PurchaseOrderItemDTO dto = new PurchaseOrderItemDTO();
        dto.setId(entity.getId());
        dto.setItemCode(entity.getItemCode());
        dto.setItemName(entity.getItemName());
        dto.setSpec(entity.getSpec());
        dto.setUnit(entity.getUnit());
        dto.setQty(defaultDecimal(entity.getQty()));
        dto.setPriceExcl(defaultDecimal(entity.getPriceExcl()));
        dto.setTaxRate(defaultDecimal(entity.getTaxRate()));
        dto.setTaxAmount(defaultDecimal(entity.getTaxAmount()));
        dto.setTotalAmount(defaultDecimal(entity.getTotalAmount()));
        dto.setNote(entity.getNote());
        return dto;
    }

    private PurchaseOrderSourceDTO toSourceDTO(final PurchaseOrderSourceEntity entity) {
        PurchaseOrderSourceDTO dto = new PurchaseOrderSourceDTO();
        dto.setId(entity.getId());
        dto.setSourceBillNo(entity.getSourceBillNo());
        dto.setSourceType(entity.getSourceType());
        dto.setSupplierName(entity.getSupplierName());
        dto.setBillDate(formatDate(entity.getBillDate()));
        dto.setTotalAmount(defaultDecimal(entity.getTotalAmount()));
        dto.setStatus(entity.getStatus());
        dto.setRemark(entity.getNote());
        return dto;
    }

    private PurchaseOrderAttachmentDTO toAttachmentDTO(final PurchaseOrderAttachmentEntity entity) {
        PurchaseOrderAttachmentDTO dto = new PurchaseOrderAttachmentDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getFileName());
        dto.setFileType(entity.getFileType());
        dto.setFileSize(entity.getFileSize());
        dto.setUploadUser(entity.getUploadUser());
        dto.setUploadTime(formatDateTime(entity.getUploadTime()));
        dto.setRemark(entity.getNote());
        return dto;
    }

    private PurchaseOrderActivityDTO toActivityDTO(final PurchaseOrderActivityEntity entity) {
        PurchaseOrderActivityDTO dto = new PurchaseOrderActivityDTO();
        dto.setId(entity.getId());
        dto.setContent(entity.getContent());
        dto.setTimestamp(formatDateTime(entity.getOperateTime()));
        dto.setOperator(entity.getOperatorName());
        dto.setType(entity.getActivityType() == null ? PurchaseOrderActivityTypeEnum.PRIMARY : entity.getActivityType());
        return dto;
    }

    /**
     * 全量替换明细行，保证服务端数据与请求保持一致
     */
    private void replaceItemList(final Long orderId,
                                 final List<PurchaseOrderItemSaveRequest> itemList,
                                 final String operatorName,
                                 final LocalDateTime now) {
        purchaseOrderMapper.deleteItemListByOrderId(orderId);
        if (itemList == null || itemList.isEmpty()) {
            return;
        }

        List<PurchaseOrderItemEntity> entityList = new ArrayList<>();
        for (int i = 0; i < itemList.size(); i++) {
            PurchaseOrderItemSaveRequest item = itemList.get(i);
            PurchaseOrderItemEntity entity = new PurchaseOrderItemEntity();
            entity.setId(YitIdHelper.nextId());
            entity.setOrderId(orderId);
            entity.setSort(i + 1);
            entity.setItemCode(item.getItemCode());
            entity.setItemName(item.getItemName());
            entity.setSpec(item.getSpec());
            entity.setUnit(item.getUnit());
            entity.setQty(defaultDecimal(item.getQty()));
            entity.setPriceExcl(defaultDecimal(item.getPriceExcl()));
            entity.setTaxRate(defaultDecimal(item.getTaxRate()));
            entity.setTaxAmount(calculateTaxAmount(item.getQty(), item.getPriceExcl(), item.getTaxRate()));
            entity.setTotalAmount(calculateTotalAmount(item.getQty(), item.getPriceExcl(), item.getTaxRate()));
            entity.setCreateBy(operatorName);
            entity.setCreateTime(now);
            entity.setUpdateBy(operatorName);
            entity.setUpdateTime(now);
            entity.setNote(item.getNote());
            entityList.add(entity);
        }
        purchaseOrderMapper.insertItemBatch(entityList);
    }

    /**
     * 新增采购订单操作日志
     */
    private void insertActivity(final Long orderId,
                                final String actionCode,
                                final String actionName,
                                final Long operatorUserId,
                                final String operatorName,
                                final String content,
                                final String activityType,
                                final LocalDateTime now) {
        PurchaseOrderActivityEntity activityEntity = new PurchaseOrderActivityEntity();
        activityEntity.setId(YitIdHelper.nextId());
        activityEntity.setOrderId(orderId);
        activityEntity.setActionCode(resolveActionEnum(actionCode));
        activityEntity.setActionName(actionName);
        activityEntity.setContent(content);
        activityEntity.setOperatorUserId(operatorUserId);
        activityEntity.setOperatorName(operatorName);
        activityEntity.setActivityType(resolveActivityType(activityType));
        activityEntity.setOperateTime(now);
        activityEntity.setCreateBy(operatorName);
        activityEntity.setCreateTime(now);
        purchaseOrderMapper.insertActivity(activityEntity);
    }

    private PurchaseOrderEntity getRequiredEntity(final Long id, final Long companyId) {
        assertCompanyId(companyId);
        if (id == null) {
            throw new BusinessException("采购订单 ID 不能为空");
        }
        PurchaseOrderEntity entity = purchaseOrderMapper.selectById(id, companyId);
        if (entity == null) {
            throw new BusinessException("采购订单不存在");
        }
        return entity;
    }

    private void validateSaveRequest(final PurchaseOrderSaveRequest request) {
        if (request == null) {
            throw new BusinessException("采购订单保存参数不能为空");
        }
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new BusinessException("采购订单明细不能为空");
        }
    }

    private void assertCompanyId(final Long companyId) {
        if (companyId == null) {
            throw new BusinessException("当前公司上下文不存在");
        }
    }

    private LocalDate parseDate(final String value, final String errorMessage) {
        if (!StringUtils.hasText(value)) {
            throw new BusinessException(errorMessage);
        }
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException ex) {
            throw new BusinessException(errorMessage);
        }
    }

    private String formatDate(final LocalDate value) {
        return value == null ? "" : value.format(DATE_FORMATTER);
    }

    private String formatDateTime(final LocalDateTime value) {
        return value == null ? "" : value.format(DATE_TIME_FORMATTER);
    }

    private String resolveStatusName(final PurchaseOrderStatusEnum status) {
        if (status == null) {
            return "";
        }
        return STATUS_NAME_MAP.getOrDefault(status, status.getLabel());
    }

    private PurchaseOrderActionEnum resolveActionEnum(final String actionCode) {
        for (PurchaseOrderActionEnum actionEnum : PurchaseOrderActionEnum.values()) {
            if (actionEnum.getValue().equals(actionCode)) {
                return actionEnum;
            }
        }
        throw new BusinessException("涓嶆敮鎸佺殑閲囪喘璁㈠崟鍔ㄤ綔");
    }

    private PurchaseOrderActivityTypeEnum resolveActivityType(final String activityType) {
        if (!StringUtils.hasText(activityType)) {
            return PurchaseOrderActivityTypeEnum.PRIMARY;
        }
        for (PurchaseOrderActivityTypeEnum typeEnum : PurchaseOrderActivityTypeEnum.values()) {
            if (typeEnum.getValue().equals(activityType)) {
                return typeEnum;
            }
        }
        return PurchaseOrderActivityTypeEnum.PRIMARY;
    }

    private String resolveSupplierName(final Long supplierId) {
        String supplierName = SUPPLIER_NAME_MAP.get(String.valueOf(supplierId));
        if (!StringUtils.hasText(supplierName)) {
            throw new BusinessException("供应商不存在");
        }
        return supplierName;
    }

    private String resolveWarehouseName(final Long warehouseId) {
        String warehouseName = WAREHOUSE_NAME_MAP.get(warehouseId);
        if (!StringUtils.hasText(warehouseName)) {
            throw new BusinessException("仓库不存在");
        }
        return warehouseName;
    }

    private String resolveUserDisplayName(final Long userId) {
        if (userId == null) {
            return "系统";
        }
        SysUserEntity userEntity = sysUserMapper.selectById(userId);
        if (userEntity == null) {
            return "系统";
        }
        if (StringUtils.hasText(userEntity.getNickname())) {
            return userEntity.getNickname();
        }
        if (StringUtils.hasText(userEntity.getUsername())) {
            return userEntity.getUsername();
        }
        return "系统";
    }

    private String generateBillNo(final Long id, final String billDateText) {
        LocalDate billDate = parseDate(billDateText, "单据日期格式不正确");
        long suffix = Math.abs(id % 10000);
        return "PO" + billDate.format(DateTimeFormatter.BASIC_ISO_DATE) + String.format("%04d", suffix);
    }

    /**
     * 根据明细行重新计算表头金额汇总
     */
    private OrderAmount calculateAmount(final List<PurchaseOrderItemSaveRequest> itemList) {
        BigDecimal totalQty = ZERO;
        BigDecimal netAmount = ZERO;
        BigDecimal taxAmount = ZERO;
        BigDecimal totalAmount = ZERO;
        for (PurchaseOrderItemSaveRequest item : itemList) {
            BigDecimal qty = defaultDecimal(item.getQty());
            BigDecimal priceExcl = defaultDecimal(item.getPriceExcl());
            BigDecimal itemTaxRate = defaultDecimal(item.getTaxRate());
            BigDecimal lineNetAmount = qty.multiply(priceExcl);
            BigDecimal lineTaxAmount = calculateTaxAmount(qty, priceExcl, itemTaxRate);
            BigDecimal lineTotalAmount = lineNetAmount.add(lineTaxAmount);
            totalQty = totalQty.add(qty);
            netAmount = netAmount.add(lineNetAmount);
            taxAmount = taxAmount.add(lineTaxAmount);
            totalAmount = totalAmount.add(lineTotalAmount);
        }
        return new OrderAmount(scaleAmount(totalQty), scaleAmount(netAmount), scaleAmount(taxAmount), scaleAmount(totalAmount));
    }

    private BigDecimal calculateTaxAmount(final BigDecimal qty, final BigDecimal priceExcl, final BigDecimal taxRate) {
        BigDecimal safeQty = defaultDecimal(qty);
        BigDecimal safePrice = defaultDecimal(priceExcl);
        BigDecimal safeTaxRate = defaultDecimal(taxRate);
        BigDecimal lineNetAmount = safeQty.multiply(safePrice);
        return scaleAmount(lineNetAmount.multiply(safeTaxRate).divide(ONE_HUNDRED, 4, RoundingMode.HALF_UP));
    }

    private BigDecimal calculateTotalAmount(final BigDecimal qty, final BigDecimal priceExcl, final BigDecimal taxRate) {
        BigDecimal lineNetAmount = defaultDecimal(qty).multiply(defaultDecimal(priceExcl));
        return scaleAmount(lineNetAmount.add(calculateTaxAmount(qty, priceExcl, taxRate)));
    }

    private BigDecimal defaultDecimal(final BigDecimal value) {
        return value == null ? ZERO : value;
    }

    private Long defaultLong(final Long value) {
        return value == null ? 0L : value;
    }

    private BigDecimal scaleAmount(final BigDecimal value) {
        return defaultDecimal(value).setScale(2, RoundingMode.HALF_UP);
    }

    private String formatDecimal(final BigDecimal value) {
        return scaleAmount(value).stripTrailingZeros().toPlainString();
    }

    private record OrderAmount(BigDecimal totalQty, BigDecimal netAmount, BigDecimal taxAmount, BigDecimal totalAmount) {
    }

    private record ActionDefinition(PurchaseOrderStatusEnum fromStatus,
                                    PurchaseOrderStatusEnum toStatus,
                                    String actionName,
                                    String activityType) {
    }

}
