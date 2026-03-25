package com.soli.system.web.controller.sysuser;

import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.Result;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserProfileDTO;
import com.soli.system.service.sysuser.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final SysUserService sysUserService;

    @GetMapping("/info")
    public Result<SysUserProfileDTO> info(@AuthenticationPrincipal Long userId) {
        SysUserDTO user = sysUserService.getById(userId)
                .orElseThrow(() -> new BusinessException("当前登录用户不存在"));

        List<String> roles = "0".equals(user.getType()) ? List.of("admin") : List.of();

        SysUserProfileDTO profile = new SysUserProfileDTO();
        profile.setId(user.getId());
        profile.setUsername(user.getUsername());
        profile.setNickname(user.getNickname());
        profile.setAvatar(user.getAvatar());
        profile.setType(user.getType());
        profile.setRoles(roles);
        return Result.success(profile);
    }
}
