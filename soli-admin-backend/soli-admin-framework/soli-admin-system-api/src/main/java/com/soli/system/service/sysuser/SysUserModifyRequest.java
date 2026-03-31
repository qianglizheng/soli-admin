package com.soli.system.service.sysuser;

import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.system.service.enums.UserSexEnum;
import com.soli.system.service.enums.UserTypeEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改用户请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-20 21:03
 */
@Getter
@Setter
public class SysUserModifyRequest {

    /** 用户 ID */
    @NotNull(message = "用户ID不能为空")
    private Long id;

    /** 用户昵称 */
    private String nickname;

    /** 用户邮箱 */
    private String email;

    /** 用户手机 */
    private String phone;

    /** 用户头像 */
    private String avatar;

    /** 用户类型 */
    private UserTypeEnum type;

    /** 用户性别 */
    private UserSexEnum sex;

    /** 用户状态 */
    private NormalDisableStatusEnum status;
}
