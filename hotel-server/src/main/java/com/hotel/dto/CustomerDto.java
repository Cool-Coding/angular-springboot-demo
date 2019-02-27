package com.hotel.dto;

import com.hotel.annotation.LoginGroup;
import com.hotel.annotation.UpdateCustomerGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Pattern;
import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-21
 * @description
 */
@Data
public class CustomerDto {
    private Integer id;

    @NotEmpty(groups = {LoginGroup.class, UpdateCustomerGroup.class},message = "客人姓名必须填写")
    @Length(max = 80)
    private String name;

    @NotEmpty(groups = {LoginGroup.class, UpdateCustomerGroup.class},message = "身份证号必须填写")
    @Pattern(groups = LoginGroup.class,regexp = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)",message = "请输入正确的身份证号")
    @Length(max = 18)
    private String idCard;

    @NotEmpty(groups = {LoginGroup.class, UpdateCustomerGroup.class},message = "客人手机号必须填写")
    @Pattern(groups = LoginGroup.class,regexp = "^[1][3,4,5,7,8][0-9]{9}$",message = "请输入正确的手机号码")
    @Length(max = 11)
    private String phoneNo;

    private List<String> roomNos;
    private String comment;
}
