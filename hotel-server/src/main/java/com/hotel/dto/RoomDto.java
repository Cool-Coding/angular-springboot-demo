package com.hotel.dto;

import com.hotel.annotation.AddRoomGroup;
import com.hotel.annotation.LoginGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 */
@Data
public class RoomDto {
    private Integer id;

    @NotEmpty(groups ={LoginGroup.class, AddRoomGroup.class}, message = "客房号必须填写")
    @Length(max = 20)
    private String roomNo;

    @NotEmpty(groups =AddRoomGroup.class, message = "客房类型必须填写")
    private String type;

    @NotNull(groups =LoginGroup.class, message = "入住日期必须填写")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date checkInDate;

    @NotNull(groups =LoginGroup.class, message = "退房日期必须填写")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") //入参
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss") //出参
    private Date   checkOutDate;


    private Integer status;

    private List<BedDto> beds;

    private List<ChargeDto> charges;

    @Valid
    private List<CustomerDto> customers;

    private String   money;

    private List<IncomeDto> incomes;
}
