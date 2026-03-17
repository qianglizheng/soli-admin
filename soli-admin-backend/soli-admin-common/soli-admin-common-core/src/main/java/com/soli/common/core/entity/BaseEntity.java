package com.soli.common.core.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 *
 *
 * @author lizhengqiang
 * @since 2026-03-07 21:13
*/
@Getter
@Setter
public abstract class BaseEntity extends IdEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = -4711040444260557604L;

    private Long id;

    /** 创建者 */
    private String createBy;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 最后更新人 */
    private String updateBy;

    /** 最后更新时间 */
    private LocalDateTime updateTime;

    /** 备注 */
    private String note;

}
