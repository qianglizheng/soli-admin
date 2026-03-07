package com.soli.system.core.entity.sysuser;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.TableName;
import com.soli.common.core.entity.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统用户持久化对象
 *
 * @author lizhengqiang
 * @since 2026-03-07 21:55
*/
@Data
@TableName("sys_user")
@EqualsAndHashCode(callSuper = true)
public class SysUserEntity extends BaseEntity {

    private String username;

    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String avatar;

    private String type;

    private String sex;

    private String status;

    private String loginIp;

    private LocalDateTime loginTime;

}
