package com.hotel.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 * @description
 */
@Data
@Builder
public class ResultDto {
    /**
     * 返回码
     */
    private String code;

    /**
     * 返回消息
     */
    private String message;

    /**
     * 是否成功
     */
    private boolean success;


    /**
     * 返回数据
     */
    private Object data;
}
