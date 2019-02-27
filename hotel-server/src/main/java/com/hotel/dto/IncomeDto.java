package com.hotel.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author guangyong.yang
 * @date 2019-01-23
 * @description
 */
@Data
public class IncomeDto {
    private Integer id;
    private String rommNo;
    private String incoming;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date logoutDate;
}
