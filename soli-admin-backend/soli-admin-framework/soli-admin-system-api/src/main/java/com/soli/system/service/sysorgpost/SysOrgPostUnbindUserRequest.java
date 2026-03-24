package com.soli.system.service.sysorgpost;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 解绑岗位员工请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostUnbindUserRequest {

    /** 岗位 ID */
    @NotNull(message = "岗位ID不能为空")
    private Long orgPostId;

    /** 用户 ID 列表 */
    @NotEmpty(message = "解绑用户列表不能为空")
    private List<Long> userIds;

}
