package com.soli.system.service.sysuser;

import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;


/**
 * @author lizhengqiang
 * @since 2026-03-20 21:04
 */
@Getter
@Setter
public class SysUserQuery extends PageQuery {

    private String username;

    private String phone;

    private String status;
}
