package com.hotel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 * @description
 */
@Data
@Builder
public class LogoutDto {
    private String roomNo;
    private String type;
    private String price;
    private String interval;
    private String sum;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date logoutDate;
}
