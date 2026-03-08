package com.soli.system.dto;

import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-08 20:50
*/
@Data
public class SysUserDTO {

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
