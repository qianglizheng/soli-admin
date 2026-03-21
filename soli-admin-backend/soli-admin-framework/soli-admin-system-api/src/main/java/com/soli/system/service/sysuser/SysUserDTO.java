package com.soli.system.service.sysuser;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 系统用户传输对象
 *
 * @author lizhengqiang
 * @since 2026-03-08 20:50
 */
@Getter
@Setter
public class SysUserDTO extends BaseDTO {

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

    /** 关联角色 ID 列表 */
    private List<Long> roleIds;
}