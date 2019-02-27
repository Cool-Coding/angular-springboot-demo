package com.hotel.controller;

import com.hotel.annotation.UpdateCustomerGroup;
import com.hotel.constant.RetCode;
import com.hotel.dto.CustomerDto;
import com.hotel.dto.ResultDto;
import com.hotel.entity.Customer;
import com.hotel.entity.Room;
import com.hotel.service.CustomerService;
import com.hotel.util.Converters;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guangyong.yang
 * @date 2019-01-27
 * 客人ResultFul接口
 */
@RestController
@Slf4j
@CrossOrigin
@RequestMapping("api")
@Api(description = "客人RestFul接口")
public class CustomerController {

    private CustomerService customerService;

    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    @ApiOperation("查询客人信息")
    public ResponseEntity<ResultDto> findCustomers(@ApiParam("客人姓名") @RequestParam(value = "name",required = false) String name,
                                                   @ApiParam("客人手机号") @RequestParam(value = "phoneNo",required = false) String phoneNo,
                                                   @ApiParam("客人身份证号") @RequestParam(value = "idCard",required = false) String idCard) {
        List<Customer> customers = customerService.findCustomers(name, phoneNo, idCard);
        if (customers == null || customers.size() == 0) {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40010)
                    .message("没有查询到客人信息")
            .build(), HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(ResultDto.builder()
        .success(true)
        .code(RetCode.RETCODE_20003)
        .message(RetCode.RETCODE_20003_MSG)
        .data(customer2Dto(customers))
        .build(),HttpStatus.OK);

    }

    @PutMapping("/customers")
    @ApiOperation("更新客人信息")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResultDto> updateCustomer(@Validated(UpdateCustomerGroup.class) @RequestBody CustomerDto customerDto) {
        // 判断客人是否存在
        Integer id = customerDto.getId();
        ResponseEntity<ResultDto> result = checkCustomerExist(id);
        if (result != null ) {
            return result;
        }

        // 判断身份证号相同的客人是否存在
        List<Customer> customerByIdCard = customerService.findCustomerByIdCard(customerDto.getIdCard());
        if (isDuplicated(customerByIdCard,customerDto)) {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40015)
                    .message("身份证号:"+customerDto.getIdCard()+"的客人已经存在")
                    .build(),HttpStatus.BAD_REQUEST);
        }


        return new ResponseEntity<>(ResultDto.builder()
                .success(true)
                .code(RetCode.RETCODE_20004)
        .message(RetCode.RETCODE_20004_MSG)
        .data(Converters.customer2Dto(customerService.update(Converters.dto2Customer(customerDto))))
        .build(),HttpStatus.OK);
    }

    @DeleteMapping("/customers/{id}")
    @ApiOperation("删除客人信息")
    @Transactional(rollbackOn = Exception.class)
    public ResponseEntity<ResultDto> deleteCustomer(@NotNull(message ="客人ID号不能为空") @PathVariable("id") Integer id) {

        ResponseEntity<ResultDto> result = checkCustomerExist(id);
        if (result != null ) {
            return result;
        }

        Customer customer = customerService.findOne(id);
        List<Room> rooms = customer.getRooms();
        if (rooms != null && rooms.size() > 0) {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40014)
                    .message("客人当前正在入住,不能删除")
                    .build(),HttpStatus.BAD_REQUEST);
        }

        customerService.delete(id);
        return new ResponseEntity<>(ResultDto.builder()
                .success(true)
                .code(RetCode.RETCODE_20004)
                .message(RetCode.RETCODE_20004_MSG)
                .build(),HttpStatus.OK);
    }

    /**
     * 判断客人是否存在
     * @param id    客人ID
     * @return      失败的话返回HTTP响应内容，成功返回null
     */
    private ResponseEntity<ResultDto> checkCustomerExist(Integer id) {
        Customer customer = customerService.findOne(id);
        if (customer == null) {
            return new ResponseEntity<>(ResultDto.builder()
                    .success(false)
                    .code(RetCode.RETCODE_40010)
                    .message("没有查询到客人信息")
                    .build(),HttpStatus.NOT_FOUND);
        }

        return null;
    }

    private boolean isDuplicated(List<Customer> customerByIdCard ,CustomerDto customerDto){
        return (customerByIdCard != null && customerByIdCard.size() > 1)
                ||(customerByIdCard.size() == 1 && !customerByIdCard.get(0).getId().equals(customerDto.getId()));
    }


    private List<CustomerDto> customer2Dto(List<Customer> customers){
        if (customers != null ) {
            List<CustomerDto> customerDtos = new ArrayList<>();
            for (Customer customer : customers) {
                customerDtos.add(Converters.customer2Dto(customer));
            }
            return customerDtos;
        }
        return null;
    }

}
