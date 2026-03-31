package com.soli.system.service.sysuser;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.soli.common.api.enums.NormalDisableStatusEnum;
import com.soli.system.service.enums.UserSexEnum;
import com.soli.system.service.enums.UserTypeEnum;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增用户请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-20 21:01
 */
@Getter
@Setter
public class SysUserCreateRequest {

    /** 用户名 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 用户密码 */
    @NotBlank(message = "用户密码不能为空")
    private String password;

    /** 用户昵称 */
    private String nickname;

    /** 用户邮箱 */
    private String email;

    /** 用户手机 */
    private String phone;

    /** 用户头像 */
    private String avatar;

    /** 用户类型 */
    @JsonSetter(nulls = Nulls.SKIP)
    private UserTypeEnum type = UserTypeEnum.NORMAL;

    /** 用户性别 */
    @JsonSetter(nulls = Nulls.SKIP)
    private UserSexEnum sex = UserSexEnum.MALE;

    /** 用户状态 */
    @JsonSetter(nulls = Nulls.SKIP)
    private NormalDisableStatusEnum status = NormalDisableStatusEnum.NORMAL;
}
