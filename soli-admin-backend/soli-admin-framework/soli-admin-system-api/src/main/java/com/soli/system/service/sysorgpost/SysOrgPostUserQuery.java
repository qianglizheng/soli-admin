package com.soli.system.service.sysorgpost;

import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位员工分页查询对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostUserQuery extends PageQuery {

    /** 岗位 ID */
    private Long orgPostId;

    /** 关键字 */
    private String keyword;

    /** 状态 */
    private String status;

}
