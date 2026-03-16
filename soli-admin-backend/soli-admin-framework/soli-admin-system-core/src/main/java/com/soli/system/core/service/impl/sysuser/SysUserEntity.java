package com.soli.system.core.service.impl.sysuser;

import java.time.LocalDateTime;

import com.soli.common.core.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 系统用户持久化对象
 *
 * @author lizhengqiang
 * @since 2026-03-07 21:55
*/
@Getter
@Setter
public class SysUserEntity extends BaseEntity {

    /** 用户名 */
    private String username;

    /** 用户密码 */
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
    private String type;

    /** 用户性别 */
    private String sex;

    /** 用户状态 */
    private String status;

    /** 用户最后登录 IP */
    private String loginIp;

    /** 用户最后登录时间 */
    private LocalDateTime loginTime;

}
