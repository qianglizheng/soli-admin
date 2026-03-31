package com.soli.system.service.sysorgpost;

import com.soli.system.service.enums.OrgTypeEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 用户可选公司对象
 *
 * @author lizhengqiang
 * @since 2026-03-26 01:10
 */
@Getter
@Setter
public class SysOrgCompanyDTO {

    /** 公司 ID */
    private Long id;

    /** 节点唯一键 */
    private String nodeKey;

    /** 公司编码 */
    private String nodeCode;

    /** 公司名称 */
    private String nodeName;

    /** 公司类型 */
    private OrgTypeEnum nodeType;

    /** 排序 */
    private Integer sort;

}
