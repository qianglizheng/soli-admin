package com.soli.system.service.sysuser;

import com.soli.system.service.enums.UserTypeEnum;
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

    private UserTypeEnum type;

    private List<String> roles;

    private Long currentCompanyId;
}
