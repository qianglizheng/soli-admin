package com.soli.business.service.purchaseorder;

import com.soli.common.api.enums.DbEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 币种枚举
 *
 * @author lizhengqiang
 * @since 2026-03-31 23:06
 */
@Getter
@RequiredArgsConstructor
public enum CurrencyEnum implements DbEnum<String> {

    CNY("CNY");

    private final String value;
}
