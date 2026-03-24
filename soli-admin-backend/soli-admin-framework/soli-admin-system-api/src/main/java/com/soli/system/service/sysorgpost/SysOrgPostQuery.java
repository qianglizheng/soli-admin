package com.soli.system.service.sysorgpost;

import com.soli.common.api.vo.PageQuery;
import lombok.Getter;
import lombok.Setter;

/**
 * 岗位分页查询对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostQuery extends PageQuery {

    /** 所属组织 ID */
    private Long orgUnitId;

    /** 父岗位 ID */
    private Long parentPostId;

    /** 岗位编码 */
    private String postCode;

    /** 岗位名称 */
    private String postName;

    /** 状态 */
    private String status;

}
