package com.soli.system.service.sysorgpost;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * 新增岗位请求对象
 *
 * @author lizhengqiang
 * @since 2026-03-24 21:00
 */
@Getter
@Setter
public class SysOrgPostCreateRequest {

    /** 所属组织 ID */
    @NotNull(message = "所属组织不能为空")
    private Long orgUnitId;

    /** 父岗位 ID */
    @JsonSetter(nulls = Nulls.SKIP)
    private Long parentPostId = 0L;

    /** 岗位编码 */
    @NotBlank(message = "岗位编码不能为空")
    private String postCode;

    /** 岗位名称 */
    @NotBlank(message = "岗位名称不能为空")
    private String postName;

    /** 岗位类型 */
    private String postType;

    /** 岗位负责人用户 ID */
    private Long managerUserId;

    /** 显示顺序 */
    @JsonSetter(nulls = Nulls.SKIP)
    private Integer sort = 1;

    /** 岗位状态 */
    @JsonSetter(nulls = Nulls.SKIP)
    private String status = "0";

    /** 备注 */
    private String note;

}
