package com.soli.system.core.service.impl.sysorgpost;

import com.soli.common.api.dto.BaseDTO;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.common.api.enums.YesNoEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位员工查询模型
 *
 * @author lizhengqiang
 * @since 2026-03-24 23:20
 */
@Getter
@Setter
public class SysOrgPostUserModel extends BaseDTO {

    /** 用户 ID */
    private Long userId;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 岗位 ID */
    private Long orgPostId;

    /** 主岗位标识 */
    private YesNoEnum primaryFlag;

    /** 用户状态 */
    private NormalDisableStatusEnum status;

}
