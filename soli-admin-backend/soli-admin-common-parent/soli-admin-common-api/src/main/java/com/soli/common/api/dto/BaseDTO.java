package com.soli.common.api.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-09 22:42
*/
@Data
public abstract class BaseDTO implements Serializable {

    /** 唯一标识 */
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