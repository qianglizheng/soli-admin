package com.soli.system.service.sysuser;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 更新用户请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-20 21:03
 */
@Getter
@Setter
public class SysUserModifyRequest {

    /** 用户ID */
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
    private String type;

    /** 用户性别 */
    private String sex;

    /** 用户状态 */
    private String status;
}