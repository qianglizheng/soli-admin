package com.soli.system.service.sysuser;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class SysUserProfileDTO {

    private Long id;

    private String username;

    private String nickname;

    private String avatar;

    private String type;

    private List<String> roles;

    private Long currentCompanyId;
}
