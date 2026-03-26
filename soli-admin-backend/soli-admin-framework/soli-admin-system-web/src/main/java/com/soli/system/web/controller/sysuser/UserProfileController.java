package com.soli.system.web.controller.sysuser;

import com.soli.auth.api.service.JwtService;
import com.soli.auth.api.service.auth.TokenDTO;
import com.soli.common.api.exception.BusinessException;
import com.soli.common.api.vo.Result;
import com.soli.common.web.security.jwt.CompanyContextHolder;
import com.soli.system.service.sysorgpost.SysOrgCompanyDTO;
import com.soli.system.service.sysorgpost.SysOrgPostService;
import com.soli.system.service.sysuser.SysUserDTO;
import com.soli.system.service.sysuser.SysUserProfileDTO;
import com.soli.system.service.sysuser.SysUserSwitchCompanyRequest;
import com.soli.system.service.sysuser.SysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "用户信息", description = "当前登录用户相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserProfileController {

    private final SysUserService sysUserService;

    private final SysOrgPostService sysOrgPostService;

    private final JwtService jwtService;

    @Operation(summary = "查询当前用户信息")
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
        profile.setCurrentCompanyId(CompanyContextHolder.getCurrentCompanyId());
        return Result.success(profile);
    }

    @Operation(summary = "查询当前用户可选公司列表")
    @GetMapping("/company/options")
    public Result<List<SysOrgCompanyDTO>> companyOptions(@AuthenticationPrincipal Long userId) {
        return Result.success(sysOrgPostService.queryUserCompanyList(userId));
    }

    @Operation(summary = "切换当前公司")
    @PutMapping("/company")
    public Result<TokenDTO> switchCompany(@AuthenticationPrincipal Long userId, @Valid @RequestBody SysUserSwitchCompanyRequest request) {
        sysOrgPostService.queryUserCompany(userId, request.getCompanyId());
        return Result.success(jwtService.generateTokenDTO(userId, request.getCompanyId()));
    }
}
