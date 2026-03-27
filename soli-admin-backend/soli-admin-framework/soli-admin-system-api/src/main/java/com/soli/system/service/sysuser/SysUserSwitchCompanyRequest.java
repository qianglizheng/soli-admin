package com.soli.system.service.sysuser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户切换公司请求
 *
 * @author lizhengqiang
 * @since 2026-03-26 01:10
 */
@Getter
@Setter
public class SysUserSwitchCompanyRequest {

    /** 公司 ID */
    @NotNull(message = "公司 ID 不能为空")
    private Long companyId;

}
