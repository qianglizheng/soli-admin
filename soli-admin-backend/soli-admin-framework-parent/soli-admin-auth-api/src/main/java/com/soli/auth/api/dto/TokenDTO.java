package com.soli.auth.api.dto;

import lombok.Data;

/**
 * @author lizhengqiang
 * @since 2026-03-08 15:50
*/
@Data
public class TokenDTO {

    /** 访问令牌 */
    private String accessToken;

    /** 刷新令牌 */
    private String refreshToken;

}
