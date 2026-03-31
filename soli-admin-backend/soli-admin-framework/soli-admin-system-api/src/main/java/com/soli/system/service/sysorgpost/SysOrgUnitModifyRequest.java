package com.soli.system.service.sysorgpost;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 修改组织单元请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-25 23:55
 */
@Getter
@Setter
public class SysOrgUnitModifyRequest {

    /** 组织单元 ID */
    @NotNull(message = "组织单元 ID 不能为空")
    private Long id;

    /** 父组织 ID */
    @NotNull(message = "上级组织不能为空")
    private Long parentId;

    /** 组织编码 */
    @NotBlank(message = "组织编码不能为空")
    private String orgCode;

    /** 组织名称 */
    @NotBlank(message = "组织名称不能为空")
    private String orgName;

    /** 负责人用户 ID */
    private Long leaderUserId;

    /** 显示顺序 */
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer sort = 1;

    /** 状态 */
    @JsonSetter(nulls = Nulls.SKIP)
    private String status = "0";

    /** 备注 */
    private String note;

}
