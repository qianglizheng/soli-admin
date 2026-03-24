package com.soli.system.service.sysorgpost;

import com.soli.common.api.dto.BaseDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位员工对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostUserDTO extends BaseDTO {

    /** 用户 ID */
    private Long userId;

    /** 用户账号 */
    private String username;

    /** 用户昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 岗位 ID */
    private Long orgPostId;

    /** 主岗位标记 */
    private String primaryFlag;

    /** 用户状态 */
    private String status;

}
