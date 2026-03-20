package com.soli.system.service.sysuser;

import lombok.Getter;
import lombok.Setter;

/**
 * 添加用户对象
 *
 * @author lizhengqiang
 * @since 2026-03-20 21:01
*/
@Getter
@Setter
public class SysUserCreateRequest {

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

}