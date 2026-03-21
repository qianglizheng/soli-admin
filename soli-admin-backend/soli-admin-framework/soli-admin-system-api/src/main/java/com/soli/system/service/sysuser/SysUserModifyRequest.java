package com.soli.system.service.sysuser;

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

    private Long id;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private String type;

    private String sex;

    private String status;
}
